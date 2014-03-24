CC = mpicc
CFLAGS = -O2 -Wall -I/usr/local/Cellar/open-mpi/1.6.5/include
LDFLAGS = -L../simple-jpeg/ -lsimplejpeg -L/usr/local/Cellar/open-mpi/1.6.5/lib -lmpi -lm

PROJ = parallel_main
OBJS = parallel_main.o

all : simple-jpeg $(PROJ)

parallel_main : $(OBJS)
	$(CC) $(CFLAGS) $< -o $@ $(LDFLAGS)

simple-jpeg :
	$(MAKE) -C ../simple-jpeg

obj-clean:
	$(RM) *.o

exec-clean:
	$(RM) $(PROJ)

autosave-clean:
	$(RM) *~

clean:
	$(MAKE) obj-clean
	$(MAKE) exec-clean
	$(MAKE) autosave-clean
