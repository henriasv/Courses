fh_A = fopen('large_matrix_a.bin');
fh_B = fopen('large_matrix_b.bin');
fh_C = fopen('mat.bin');

rows_A = fread(fh_A, 1, 'int')
cols_A = fread(fh_A, 1, 'int')

A = fread(fh_A, [cols_A,rows_A], 'double');
A = A';

rows_B = fread(fh_B, 1, 'int')
cols_B = fread(fh_B, 1, 'int')

B = fread(fh_B, [cols_B,rows_B], 'double');
B = B';





rows_C = fread(fh_C, 1, 'int')
cols_C = fread(fh_C, 1, 'int')
C = fread(fh_C, [cols_C,rows_C], 'double');
C = C';

D = A*B-C;

sum(sum(D))