server_singleplayer: compile-singleplayer
	cd SINGLE && java -cp src server.SinglePlayerServer

client_singleplayer: compile-singleplayer
	cd SINGLE && java -cp src client.Client netprog1.csit.rmit.edu.au 61231

server_multiplayer: compile-multiplayer
	cd MULTI && java -cp src server.MultiPlayerServer

client_multiplayer: compile-multiplayer
	cd MULTI && java -cp src client.Client netprog1.csit.rmit.edu.au 61231

compile: compile-singleplayer compile-multiplayer

compile-singleplayer: 
	javac -d SINGLE/src SINGLE/src/*/*.java

compile-multiplayer:
	javac -d MULTI/src MULTI/src/*/*.java
