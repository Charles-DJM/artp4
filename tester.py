#!/usr/bin/env python3
import subprocess

# Liste de fichiers tests
msg_list = "vide lignevide 1car 10car 64car 1024car 1Mcar".split(" ")
port_server = 1234

def run_test_server(server_prg, client_prg, msg_list):
    # Start server
    cmd_server = [ "./pm_daemonize.sh", "java", server_prg, str(port_server) ]
    server = subprocess.run(cmd_server)

    # Start client for every msg in msg_list
    print( "*** Testing " + server_prg + " with " + client_prg )
    for m in msg_list:
        cmd_client = [ "java", client_prg, "localhost", str(port_server) ]
        client = subprocess.run(cmd_client, stdin=open(m))
        if bool(client.returncode):
            print("FAILED : " + server_prg + " with " + client_prg + " for " + m)
        print()

# Main test loop
server_list = [ "EchoServer" ]
client_list = [ "EchoClient" ]
for s in server_list:
    for c in client_list:
        run_test_server(s,c,msg_list)
                  
