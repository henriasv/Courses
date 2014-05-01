#include <stdio.h>
#include <stdlib.h>
#include <math.h>
//#include <mpi.h>



typedef struct 
{
	double** data;
	int num_rows;
	int num_cols;

} matrix;

void read_matrix_binary(char* filename, matrix* mat);
void write_matrix_binary(char* filename, matrix* mat);
void allocate_matrix(matrix* mat, int num_rows, int num_cols);
void print_matrix(matrix* mat);
void addMulitiplyMatrices(matrix* C, matrix* A, matrix* B);
void split_matrix_col_major(matrix** submatrices, int** corner_indices, int* num_rows_submatrix, int* num_cols_submatrix, matrix* C, int num_procs);
void add_to_matrix(matrix* to, matrix* from);
void add_multiply_submatrix_arrays_cannon(matrix** submatrices_C, matrix** submatrices_A, matrix**submatrices_B, int num_procs);
int cannon_shift_A_col_major(int pos, int sq_p);
int cannon_initial_position_A_col_major(int pos, int sq_p);
int cannon_shift_B_col_major(int pos, int sq_p);
int cannon_initial_position_B_col_major(int pos, int sq_p);
void assemble_matrix(matrix* C, matrix** submatrices, int** corner_indices, int num_procs);

int main(int argc, char* argv[]){

	int num_procs = 25;
	// Three matrices
	matrix A;
	matrix B;
	matrix C;
	int i;

	int num_rows_submatrix_A;
	int num_cols_submatrix_A;

	int num_rows_submatrix_B;
	int num_cols_submatrix_B;

	int num_rows_submatrix_C;
	int num_cols_submatrix_C;

	matrix** submatrices_A = (matrix**) malloc(num_procs*sizeof(matrix*));
	submatrices_A[0] = (matrix*) malloc(num_procs*sizeof(matrix));
	int** corner_indices_A = (int**) malloc(num_procs*sizeof(int*));	
	corner_indices_A[0] = (int*) malloc(num_procs*sizeof(int)*2);

	for (i = 1; i<num_procs; i++) {
		submatrices_A[i] = submatrices_A[i-1] + 1;
		corner_indices_A[i] = corner_indices_A[i-1] + 2;
	}

	matrix** submatrices_B = (matrix**) malloc(num_procs*sizeof(matrix*));
	submatrices_B[0] = (matrix*) malloc(num_procs*sizeof(matrix));
	int** corner_indices_B = (int**) malloc(num_procs*sizeof(int*));	
	corner_indices_B[0] = (int*) malloc(num_procs*sizeof(int)*2);

	for (i = 1; i<num_procs; i++) {
		submatrices_B[i] = submatrices_B[i-1] + 1;
		corner_indices_B[i] = corner_indices_B[i-1] + 2;
	}

	matrix** submatrices_C = (matrix**) malloc(num_procs*sizeof(matrix*));
	submatrices_C[0] = (matrix*) malloc(num_procs*sizeof(matrix));
	int** corner_indices_C = (int**) malloc(num_procs*sizeof(int*));	
	corner_indices_C[0] = (int*) malloc(num_procs*sizeof(int)*2);

	for (i = 1; i<num_procs; i++) {
		submatrices_C[i] = submatrices_C[i-1] + 1;
		corner_indices_C[i] = corner_indices_C[i-1] + 2;
	}

	read_matrix_binary("small_matrix_a.bin", &A);
	read_matrix_binary("small_matrix_b.bin", &B);
	allocate_matrix(&C, A.num_rows, B.num_cols);

	write_matrix_binary("mat_A.bin", &A);
	write_matrix_binary("mat_B.bin", &B);
	//write_matrix_binary("mat.bin", &C);

	//split_matrix_col_major()
	split_matrix_col_major(submatrices_A, corner_indices_A, &num_rows_submatrix_A, &num_cols_submatrix_A, &A, num_procs);
	split_matrix_col_major(submatrices_B, corner_indices_B, &num_rows_submatrix_B, &num_cols_submatrix_B, &B, num_procs);
	split_matrix_col_major(submatrices_C, corner_indices_C, &num_rows_submatrix_C, &num_cols_submatrix_C, &C, num_procs);

	add_multiply_submatrix_arrays_cannon(submatrices_C, submatrices_A, submatrices_B, num_procs);

/*
	for (i = 0; i<num_procs; i++) {
		printf("%d\n", i);
		print_matrix(submatrices_C[i]);
		printf("corner_indices_C[%d][0] = %d\n", i, corner_indices_C[i][0]);
		printf("corner_indices_C[%d][1] = %d\n", i, corner_indices_C[i][1]);
	}
	*/

	assemble_matrix(&C, submatrices_C, corner_indices_C, num_procs);
	write_matrix_binary("mat.bin", &C);
	//print_matrix(&C);


	//print_matrix(&A);
	//print_matrix(&B);
	//print_matrix(&C);
}

void read_matrix_binary(char* filename, matrix* mat) {
	int i;
	FILE* fp = fopen(filename, "rb");
	fread(&(mat->num_rows), sizeof(int), 1, fp);
	fread(&(mat->num_cols), sizeof(int), 1, fp);

	printf("Reading matrix: %d columns, %d rows \n", mat->num_cols, mat->num_rows);

	/* storage allocation of matrix */
	mat->data = (double**) malloc((mat->num_rows)*sizeof(double*));
	(mat->data)[0] = (double*)malloc((mat->num_rows)*(mat->num_cols)*sizeof(double));
	for (i = 1; i<(mat->num_rows); i++) {
		(mat->data)[i] = (mat->data)[i-1]+(mat->num_cols);
	}

	/* Read matrix */
	fread((mat->data)[0], sizeof(double), (mat->num_rows)*(mat->num_cols), fp);
	fclose(fp);
}

void write_matrix_binary(char* filename, matrix* mat) {
	FILE* fp = fopen(filename, "wb");
	fwrite(&(mat->num_rows), sizeof(int), 1, fp);
	fwrite(&(mat->num_cols), sizeof(int), 1, fp);
	fwrite((mat->data)[0], sizeof(double), (mat->num_rows)*(mat->num_cols), fp);

}

void allocate_matrix(matrix* mat, int num_rows, int num_cols) {
	int i, j;

	mat->num_rows = num_rows;
	mat->num_cols = num_cols;

	mat->data = (double**) malloc((mat->num_rows)*sizeof(double*));
	(mat->data)[0] = (double*)malloc((mat->num_rows)*(mat->num_cols)*sizeof(double));
	for (i = 1; i<(mat->num_rows); i++) {
		(mat->data)[i] = (mat->data)[i-1]+(mat->num_cols);
	}
	for (i = 0; i<(mat->num_rows); i++) {
		for (j=0;j<(mat->num_cols); j++) {
			mat->data[i][j] = 0;
		}
	}
}

void print_matrix(matrix* mat) {
	int i, j;
	printf("Printing matrix with %d columns, %d rows\n", mat->num_cols, mat->num_rows);
	for (i = 0; i<(mat->num_rows); i++) {
		for (j = 0; j<(mat->num_cols); j++) {
			printf("%6.2f", (mat->data)[i][j]);
		}
		printf("\n");
	}
}


void addMulitiplyMatrices(matrix* C, matrix* A, matrix* B) {
	/* Check if dimensions are OK for multiplication */
	if (A->num_cols != B->num_rows) {
		printf("num_cols of A != num_rows of B\n");
		exit(1);
	}
	if (C->num_rows != A->num_rows) {
		printf("num_rows of C != num_rows of A\n");
		exit(1);
	}
	if (C->num_cols < B->num_cols) {
		printf("num_cols of C < num_cols of B\n");
		exit(1);
	}

	int i, j, k;

	for (i = 0; i<A->num_rows; i++) {
		for (j = 0; j<B->num_cols; j++) {
			for (k = 0; k<A->num_cols; k++) {
				(C->data)[i][j] += (A->data)[i][k]*(B->data)[k][j];
			}
		}
	}

}

void split_matrix_col_major(matrix** submatrices, int** corner_indices, int* num_rows_submatrix, int* num_cols_submatrix, matrix* C, int num_procs) {

	if ((int)sqrt(num_procs)*(int)sqrt(num_procs) != num_procs) {
		printf("cannot split matrix for non-quadratic number of processes\n");
		exit(1);
	}

	int m;
	for (m = 0; m>num_procs; m++) {
		corner_indices[m][0] = 0;
		corner_indices[m][1] = 0;
	}

	int sq_p = sqrt(num_procs);

	int num_cols = C->num_cols;
	int num_rows = C->num_rows;


	int rows_per_proc_estimate = num_rows/sq_p;
	int cols_per_proc_estimate = num_cols/sq_p;
	*num_cols_submatrix = cols_per_proc_estimate+1;
	*num_rows_submatrix = rows_per_proc_estimate+1;

	int row_remainder = num_rows%sq_p;
	int col_remainder = num_cols%sq_p;

	int i, j, k, l;
	int rows_per_proc, cols_per_proc;

	int row_counter = 0;
	int col_counter = 0;
	for (i = 0; i<sq_p; i++) {
			if (i<col_remainder)
				cols_per_proc = cols_per_proc_estimate+1;
			else 
				cols_per_proc = cols_per_proc_estimate;
		for (j=0; j<sq_p; j++) {
			if (j<row_remainder)
				rows_per_proc = rows_per_proc_estimate+1;
			else
				rows_per_proc = rows_per_proc_estimate;
			printf("%d %d\n", i, j);
			corner_indices[i*sq_p+j][0] = row_counter;
			corner_indices[i*sq_p+j][1] = col_counter;
			allocate_matrix(submatrices[i*sq_p+j], *num_rows_submatrix, *num_cols_submatrix);
			for (k = row_counter; k<row_counter+rows_per_proc; k++){
				for (l = col_counter; l<col_counter+cols_per_proc; l++) {
					submatrices[i*sq_p+j]->data[k-row_counter][l-col_counter] = C->data[k][l];
				}
			}
			row_counter += rows_per_proc;
		}
		row_counter = 0;
		col_counter += cols_per_proc;
	}

}

void add_to_matrix(matrix* to, matrix* from) {
	if (to->num_cols<=from->num_cols && to->num_rows<=from->num_rows) {
		int i, j;
		for (i = 0; i<from->num_rows; i++) {
			for (j = 0; j<from->num_cols; j++) {
				to->data[i][j] += from->data[i][j];
			}
		}
	}
	else {
		printf("Cannot add to matrix because of dimension mismatch\n");
		exit(1);
	}
}

void add_multiply_submatrix_arrays_cannon(matrix** submatrices_C, matrix** submatrices_A, matrix**submatrices_B, int num_procs) {
	int* grid_positions_A = (int*) malloc(num_procs*sizeof(int));
	int* grid_positions_B = (int*) malloc(num_procs*sizeof(int));

	int sq_p = sqrt(num_procs);

	int i,j, k;
	for (i=0; i<num_procs; i++) {
		grid_positions_A[i] = cannon_initial_position_A_col_major(i, sq_p);
		grid_positions_B[i] = cannon_initial_position_B_col_major(i, sq_p);
	}

	printf("grid_positions_A\n");
	for (j = 0; j<num_procs; j++) {
		printf("%d\n", grid_positions_A[j]);
	}
	printf("grid_positions_B\n");
	for (j = 0; j<num_procs; j++) {
		printf("%d\n", grid_positions_B[j]);
	}

	int current_proc;
	int B_index;
	for (i=0; i<sq_p; i++) {
		for (j = 0; j<num_procs; j++) {
			current_proc = grid_positions_A[j];
			for (k = 0; k<num_procs; k++) {
				if (grid_positions_B[k] == current_proc) {
					B_index = k;
				}
			}
			addMulitiplyMatrices(submatrices_C[current_proc], submatrices_A[j], submatrices_B[B_index]);
		}
		for (j=0; j<num_procs; j++) {
			grid_positions_A[j] = cannon_shift_A_col_major(grid_positions_A[j], sq_p);
			grid_positions_B[j] = cannon_shift_B_col_major(grid_positions_B[j], sq_p);
		}

		printf("grid_positions_A\n");
		for (j = 0; j<num_procs; j++) {
			printf("%d\n", grid_positions_A[j]);
		}
		printf("grid_positions_B\n");
		for (j = 0; j<num_procs; j++) {
			printf("%d\n", grid_positions_B[j]);
		}
	}
}

void assemble_matrix(matrix* C, matrix** submatrices, int** corner_indices, int num_procs) {
	int i, k, l;
	int num_cols, num_rows, global_row, global_col;
	double data_number;
	int sq_p = sqrt(num_procs);
	for (i = 0; i<num_procs; i++) {
		num_cols = submatrices[i]->num_cols;
		num_rows = submatrices[i]->num_rows;
		global_row = corner_indices[i][0];
		global_col = corner_indices[i][1];

		for (k = 0; k<num_rows; k++) {
			for (l = 0; l<num_cols; l++) {
				data_number = submatrices[i]->data[k][l];
				if (data_number != 0) {
					C->data[global_row+k][global_col+l] += data_number;
				}
			}
		}

	}
}

int cannon_shift_A_col_major(int pos, int sq_p) {
	int new_pos = (pos-sq_p+sq_p*sq_p)%(sq_p*sq_p);
	return new_pos;
}

int cannon_shift_B_col_major(int pos, int sq_p) {
	int new_pos = (pos/sq_p)*sq_p + (pos%sq_p-1+sq_p)%sq_p;
	return new_pos;
}

int cannon_initial_position_A_col_major(int pos, int sq_p){
	return (pos - (pos%sq_p)*sq_p + sq_p*sq_p) % (sq_p*sq_p);
}

int cannon_initial_position_B_col_major(int pos, int sq_p){
	return (pos/sq_p)*sq_p + (pos%sq_p-pos/sq_p + sq_p)%sq_p;
}














