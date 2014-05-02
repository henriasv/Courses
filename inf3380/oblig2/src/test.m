fh_A = fopen('small_matrix_a.bin');
fh_B = fopen('small_matrix_b.bin');
fh_C = fopen('mat.bin');
fh_B_test = fopen('mat_B.bin');

rows_A = fread(fh_A, 1, 'int')
cols_A = fread(fh_A, 1, 'int')

A = fread(fh_A, [cols_A,rows_A], 'double');
A = A';

rows_B = fread(fh_B, 1, 'int')
cols_B = fread(fh_B, 1, 'int')

B = fread(fh_B, [cols_B,rows_B], 'double');
B = B';


rows_B_test = fread(fh_B_test, 1, 'int')
cols_B_test = fread(fh_B_test, 1, 'int')
B_test = fread(fh_B_test, [cols_B_test, rows_B_test], 'double');
B_test = B_test';



rows_C = fread(fh_C, 1, 'int')
cols_C = fread(fh_C, 1, 'int')
C = fread(fh_C, [cols_C,rows_C], 'double');
C = C';

D = A*B-C;

sum(sum(B-B_test))
sum(sum(D))