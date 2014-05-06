import subprocess
cmd = ['mpirun','-n', '2', './parallel_main', '100','0.24', '../mona_lisa_noisy.jpg', '../mona_lisa_noisy_denoised_parallell.jpg'];
cmd_for_subprocess = "";
for word in cmd:
	cmd_for_subprocess += word
	cmd_for_subprocess += " "
print cmd_for_subprocess;
subprocess.call(cmd_for_subprocess, shell=True)