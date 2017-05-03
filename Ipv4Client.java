import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Ipv4Client {

	static Socket socket = null;
	static BufferedReader in = null;
	static OutputStream out = null;
	static byte[] ipPacket = new byte[20 + 4096];

	public static void main(String[] args) throws Exception {
		socket = new Socket("codebank.xyz", 38003);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		out = socket.getOutputStream();

		ipPacket[0] = (byte) 0x45; // version and HeaderLength 0010 0101
		ipPacket[6] = (byte) 0x40; // flags 0100 0000
		ipPacket[8] = (byte) 50; // TTL in seconds
		ipPacket[9] = (byte) 0x06; // TCP protocol number
		
		//server ip address
		byte[] ip = socket.getInetAddress().getAddress();
		ipPacket[16] = ip[0];
		ipPacket[17] = ip[1];
		ipPacket[18] = ip[2];
		ipPacket[19] = ip[3];

		for (int i = 2; i <= 4096; i *= 2) {
			ipPacket[2] = (byte) ((i + 20) >> 8); //total length upper
			ipPacket[3] = (byte) ((i + 20) & 0xff); //total length lower
			ipPacket[10] = 0;//zero out the checksum
			ipPacket[11] = 0;
			short checksum = checksum(ipPacket,20);
			ipPacket[10] = (byte) (checksum >> 8);//write the checksum
			ipPacket[11] = (byte) (checksum & 0xff);
			
			System.out.println("Sending packet length: " + i);
			out.write(ipPacket, 0, 20 + i); //send the bytes
			System.out.println(in.readLine()); //read the response
			System.out.println();
		}
	}

	// perform checksum
	public static short checksum(byte[] b,int size) {
		long sum = 0;
		for (int i = 0; i < size; i += 2) {
			sum += (b[i] << 8) + (b[i + 1] & 0xff);
			if ((sum & 0xFFFF0000) != 0) {

				sum &= 0xFFFF;
				sum++;
			}
		}
		return (short) ~(sum & 0xFFFF);
	}
}
