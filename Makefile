# On Linux, these commands will need to run with root privileges. Just invoke them manually.
all:
	docker build -t jayhorn_docker .
	docker run -it jayhorn_docker