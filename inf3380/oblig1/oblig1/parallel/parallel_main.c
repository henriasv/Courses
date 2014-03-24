#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <string.h>

typedef struct
{
  float** image_data;  /* a 2D array of floats */
  int m;               /* # pixels in x-direction */
  int n;               /* # pixels in y-direction */
} image;

void allocate_image(image *u, int m, int n);
void deallocate_image(image *u);
void convert_jpeg_to_image(const unsigned char* image_chars, image *u);
void convert_image_to_jpeg(const image *u, unsigned char* image_chars);
void iso_diffusion_denoising(image *u, image *u_bar, float kappa);

int main(int argc, char* argv[]) {
	int m, n, c, iters;
	int my_m, my_n, last_m,my_rank, num_procs;
	float kappa;
	image u, u_bar;
	unsigned char* image_chars, *my_image_chars;
	char *input_jpeg_filename, *output_jpeg_filename;

	MPI_Init (&argc, &argv);
	MPI_Comm_rank (MPI_COMM_WORLD, &my_rank);
	MPI_Comm_size (MPI_COMM_WORLD, &num_procs);

	MPI_Status status;

  /* read from command line: kappa, iters, input_jpeg_filename, output_jpeg_filename */
  /* ... */
	if (argc == 5) {
		kappa = atof(argv[1]);
		iters = atoi(argv[2]);
		input_jpeg_filename = argv[3];
		output_jpeg_filename = argv[4];
		if (my_rank == 0) {
		printf("iters %d \nkappa %f \ninput filename %s \noutput filename %s \n", iters
			, kappa, input_jpeg_filename, output_jpeg_filename);
	}
	}
	else {
		printf("Usage: ./serial_main iters kappa input_filename output_filename\n");
		MPI_Finalize();
		exit(0);
	}

	if(my_rank == 0) {
		import_JPEG_file(input_jpeg_filename, &image_chars, &m, &n, &c);
	}

	MPI_Bcast(&m, 1, MPI_INT, 0, MPI_COMM_WORLD);
	MPI_Bcast(&n, 1, MPI_INT, 0, MPI_COMM_WORLD);

	my_m = m/num_procs;
	my_n = n;
	if (my_rank == num_procs-1) {
		last_m = m-my_m*(num_procs-1);
		my_m = last_m;
	}
	MPI_Bcast(&last_m, 1, MPI_INT, num_procs-1, MPI_COMM_WORLD);

	my_image_chars = malloc(my_m*my_n*sizeof(MPI_UNSIGNED_CHAR));
	printf("rank %d, my_m %d \n", my_rank, my_m);

	if (my_rank == 0) {
		int i;

		for (i=1; i<num_procs-1; i++) {
			MPI_Send(&image_chars[i*my_n*my_m], my_n*my_m, MPI_UNSIGNED_CHAR, i, 0, MPI_COMM_WORLD);
		}

		MPI_Send(&image_chars[i*my_n*my_m], last_m*my_n, MPI_UNSIGNED_CHAR, i, 0, MPI_COMM_WORLD);
		memcpy(my_image_chars, image_chars, my_m*my_n*sizeof(unsigned char));
	}

	if (my_rank != 0) {
		MPI_Recv(my_image_chars, my_n*my_m, MPI_UNSIGNED_CHAR, 0, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
	}

	MPI_Barrier(MPI_COMM_WORLD);

	allocate_image(&u, my_m, my_n);
	allocate_image(&u_bar, my_m, my_n);

	convert_jpeg_to_image(my_image_chars, &u);
	for (int i = 0; i<iters; i++){
		iso_diffusion_denoising(&u, &u_bar, kappa);
		if (my_rank == 0) printf("Iteration %d\n", i);
		// Communication

		if (my_rank < num_procs-1) {
			MPI_Send(u.image_data[my_m-2], my_n, MPI_FLOAT, my_rank+1, 0, MPI_COMM_WORLD);	
		}

		if (my_rank > 0) {
			MPI_Recv(u.image_data[0], my_n, MPI_FLOAT, my_rank-1, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
		}
		
		if (my_rank > 0) {
			MPI_Send(u.image_data[1], my_n, MPI_FLOAT,my_rank-1,0, MPI_COMM_WORLD); 
		}
		
		if (my_rank < num_procs-1)
			MPI_Recv(u.image_data[my_m-1], my_n, MPI_FLOAT, my_rank+1, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
		

	} 
	convert_image_to_jpeg(&u, my_image_chars);

	if (my_rank != 0) MPI_Send(my_image_chars, my_m*my_n, MPI_UNSIGNED_CHAR, 0, 0, MPI_COMM_WORLD);


	if (my_rank == 0) {
		memcpy(image_chars, my_image_chars, my_m*my_n*sizeof(unsigned char));
		int i;
		for (i=1; i<num_procs-1; i++) {
			MPI_Recv(&image_chars[i*my_n*my_m], my_m*my_n, MPI_UNSIGNED_CHAR, i, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
		}
		MPI_Recv(&image_chars[i*my_n*my_m], my_n*last_m, MPI_UNSIGNED_CHAR, i, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
	}

	MPI_Barrier(MPI_COMM_WORLD);

	if (my_rank == 0) {
		export_JPEG_file(output_jpeg_filename, image_chars, m, n, c, 75);
	}

	deallocate_image(&u);
	deallocate_image(&u_bar);

	MPI_Finalize();
	if (my_rank == 0) printf("Done, output file: \n%s\n", output_jpeg_filename);

	return 0;
}

void allocate_image(image *u, int m, int n) {
	int i;
	u->m = m; u->n=n;
	u->image_data = malloc(m*sizeof(double*));
	for (i = 0; i<m; i++) {
		u->image_data[i] = malloc(n*sizeof(double));
	}
}
void deallocate_image(image *u){
	int i;
	for (i=0; i<u->m; i++) {
		free(u->image_data[i]);
	}
}
void convert_jpeg_to_image(const unsigned char* image_chars, image *u){
	int m = u->m;
	int n = u->n;
	int i, j;
	for (i = 0; i<m; i++){
		for (j = 0; j<n; j++) {
			u->image_data[i][j] = image_chars[i*n+j];
		}
	}
}
void convert_image_to_jpeg(const image *u, unsigned char* image_chars){
	int m = u->m;
	int n = u->n;
	int i, j;
	for (i = 0; i<m; i++) {
		for (j=0; j<n; j++) {
			image_chars[i*n+j] = u->image_data[i][j];
		}
	}
}

void iso_diffusion_denoising(image *u, image *u_bar, float kappa){
	int i, j; // Loop variables
	int m = u->m;
	int n = u->n;
	float** u_bar_array = u_bar->image_data;
	float** u_array = u->image_data;
	
		for (i=1; i<m-1; i++) {
			for (j=1; j<n-1; j++) {
				u_bar_array[i][j] = u_array[i][j] + kappa*(u_array[i-1][j] + u_array[i][j-1] - 4*u_array[i][j] + u_array[i][j+1] + u_array[i+1]
					[j]);
			}
		}
		for (i = 0; i<m; i++) {
			u_bar_array[i][0] = u_array[i][0];
			u_bar_array[i][n-1] = u_array[i][n-1];
		}
		for (j = 0; j<n; j++) {
			u_bar_array[0][j] = u_array[0][j];
			u_bar_array[m-1][j] = u_array[m-1][j];
		}

		for (i=0; i<m; i++) {
			for (j=0; j<n; j++) {
				u_array[i][j] = u_bar_array[i][j];
			}
		}
}