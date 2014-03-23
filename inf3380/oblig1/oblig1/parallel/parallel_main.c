#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>  

typedef struct
{
  float** image_data;  /* a 2D array of floats */
  int m;               /* # pixels in x-direction */
  int n;               /* # pixels in y-direction */
} image;

int main(int argc, char* argv[]) {
	int m, n, c, iters;
	int my_m, my_n, my_rank, num procs;
	float kappa;
	image u, u_bar, whole_image;
	unsigned char* image_chars, my_image_chars;
	char *input_jpeg_filename, *output_jpeg_filename;

	MPI_Init (&argc, &argv);
	MPI_Comm_Rank (MPI_COMM_WORLD);
	MPI_Comm_Size (MPI_COMM_WORLD);



	return 0;
}