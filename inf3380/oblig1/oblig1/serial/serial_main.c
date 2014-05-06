#include <stdio.h>
#include <stdlib.h>


// make use of two functions from the simplejpeg library
/*
void import_JPEG_file(const char *filename, unsigned char **image_chars,
	int *image_height, int *image_width,
	int *num_components);

void export_JPEG_file(const char *filename, unsigned char *image_chars,
	int image_height, int image_width,
	int num_components, int quality);
*/
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
void iso_diffusion_denoising(image *u, image *u_bar, float kappa, int iters);

int main(int argc, char *argv[])
{
	int m, n, c, iters;
	float kappa;
	image u, u_bar;
	unsigned char *image_chars;
	char *input_jpeg_filename, *output_jpeg_filename;
  /* read from command line: kappa, iters, input_jpeg_filename, output_jpeg_filename */
  /* ... */
	if (argc == 5) {
		kappa = atof(argv[2]);
		iters = atoi(argv[1]);
		input_jpeg_filename = argv[3];
		output_jpeg_filename = argv[4];
		printf("iters %d \n kappa %f \n input filename %s \n output filename %s \n", iters
			, kappa, input_jpeg_filename, output_jpeg_filename);
	}
	else {
		printf("Usage: ./serial_main iters kappa input_filename output_filename\n");
		exit(1);
	}

	import_JPEG_file(input_jpeg_filename, &image_chars, &m, &n, &c);
	allocate_image (&u, m, n);
	allocate_image (&u_bar, m, n);
	convert_jpeg_to_image (image_chars, &u);
	iso_diffusion_denoising (&u, &u_bar, kappa, iters);
	convert_image_to_jpeg (&u_bar, image_chars);
	export_JPEG_file(output_jpeg_filename, image_chars, m, n, c, 75);
	deallocate_image (&u);
	deallocate_image (&u_bar);
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
	printf("Converting JPEG to image \n");
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
void iso_diffusion_denoising(image *u, image *u_bar, float kappa, int iters){
	int i, j, k; // Loop variables
	int m = u->m;
	int n = u->n;
	float** u_bar_array = u_bar->image_data;
	float** u_array = u->image_data;
	
	for (k = 0; k<iters; k++) {
		printf("Iteration number %d \n", k);
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
}
