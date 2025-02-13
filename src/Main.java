import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt user for IP address input
        System.out.println("IP-Adresse Eingeben: (xxx.xxx.xxx.xxx)");
        String ipAdr = scanner.nextLine();

        // Prompt user for subnet input
        System.out.println("Subnetz Eingeben: (xxx.xxx.xxx.xxx)");
        String subnet = scanner.nextLine();

        // Validate the entered IP and subnet format
        if (!isValidIP(ipAdr) || !isValidIP(subnet)) {
            System.out.println("Ungültige IP oder Subnetz!");
            System.exit(0);
        }

        // Split IP and subnet into string arrays
        String[] splitSubnet = subnet.split("\\.");
        String[] splitIp = ipAdr.split("\\.");

        // Boolean arrays to store binary representation of IP and subnet
        boolean[] ipAdrBin = new boolean[32];
        boolean[] ipSubnetBin = new boolean[32];

        // StringBuilder to store binary representation of subnet
        StringBuilder binarySubnet = new StringBuilder();

        // Convert each octet of IP and subnet into binary and store in arrays
        for (int i = 0; i < 4; i++) {
            String binIp = decToBinary(Integer.parseInt(splitIp[i]));
            String binSubnet = decToBinary(Integer.parseInt(splitSubnet[i]));
            binarySubnet.append(binSubnet).append(" ");
            System.arraycopy(stringToBin(binIp), 0, ipAdrBin, i * 8, 8);
            System.arraycopy(stringToBin(binSubnet), 0, ipSubnetBin, i * 8, 8);
        }

        // Calculate Network ID and Broadcast Address
        String netzID = calculateAddress(ipAdrBin, ipSubnetBin, true);
        String broadcastID = calculateAddress(ipAdrBin, ipSubnetBin, false);

        // Calculate number of usable client IPs
        int UsableClientsint = calculateUsableClients(ipSubnetBin);

        // Display results
        System.out.println("SUBNET-ID-BINARY:    " + binarySubnet.toString().trim());
        System.out.println("NETZ-ID-BINARY:      " + booleanArrayToBinary(ipAdrBin, ipSubnetBin, true));
        System.out.println("BROADCAST-ID-BINARY: " + booleanArrayToBinary(ipAdrBin, ipSubnetBin, false));
        System.out.println("");
        System.out.println("NETZ-ID:             " + netzID);
        System.out.println("Broadcast-ID:        " + broadcastID);
        System.out.println("SUBNET-IP:           " + subnet);
        System.out.println("USABLE-CLIENTS:      " + UsableClientsint);
    }

    // Calculate either Network ID (AND operation) or Broadcast Address (OR with inverted Subnet Mask)
    static String calculateAddress(boolean[] ipAdr, boolean[] ipSubnet, boolean isNetworkID) {
        StringBuilder resultBinary = new StringBuilder();

        for (int i = 0; i < ipAdr.length; i++) {
            if (isNetworkID) {
                resultBinary.append(ipAdr[i] & ipSubnet[i] ? "1" : "0"); // Bitwise AND
            } else {
                resultBinary.append(ipAdr[i] | !ipSubnet[i] ? "1" : "0"); // Bitwise OR with NOT subnet mask
            }
        }

        return binaryToIP(resultBinary.toString());
    }

    // Calculate the number of usable clients in the subnet
    static int calculateUsableClients(boolean[] ipSubnet) {
        int zerosInBinary = 0;
        for (boolean bit : ipSubnet) {
            if (!bit) {
                zerosInBinary++;
            }
        }
        return (int) Math.pow(2, zerosInBinary) - 2; // Subtract 2 for Network ID and Broadcast Address
    }

    // Convert binary string to standard IP format
    static String binaryToIP(String binary) {
        String[] octets = binary.split("(?<=\\G.{8})"); // Split into 8-bit chunks
        StringBuilder ip = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            ip.append(Integer.parseInt(octets[i], 2)); // Convert binary to decimal
            if (i < 3) ip.append(".");
        }

        return ip.toString();
    }

    // Convert decimal number to 8-bit binary string
    static String decToBinary(int n) {
        return String.format("%8s", Integer.toBinaryString(n)).replace(' ', '0');
    }

    // Convert binary string into a boolean array
    static boolean[] stringToBin(String binString) {
        boolean[] binArray = new boolean[binString.length()];
        for (int i = 0; i < binString.length(); i++) {
            binArray[i] = binString.charAt(i) == '1';
        }
        return binArray;
    }

    // Validate IP format using regex
    static boolean isValidIP(String ip) {
        String regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
        return Pattern.matches(regex, ip);
    }

    // Convert boolean arrays to binary string format
    static String booleanArrayToBinary(boolean[] ipAdr, boolean[] ipSubnet, boolean isNetworkID) {
        StringBuilder resultBinary = new StringBuilder();
        for (int i = 0; i < ipAdr.length; i++) {
            if (isNetworkID) {
                resultBinary.append(ipAdr[i] & ipSubnet[i] ? "1" : "0");
            } else {
                resultBinary.append(ipAdr[i] | !ipSubnet[i] ? "1" : "0");
            }
            if ((i + 1) % 8 == 0 && i != 31) {
                resultBinary.append(" "); // Add space between octets
            }
        }
        return resultBinary.toString();
    }
}
