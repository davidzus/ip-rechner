import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("IP-Adresse Eingeben:");
        String ipAdr = scanner.nextLine();

        System.out.println("Subnetz Eingeben:");
        String subnet = scanner.nextLine();

        if (!isValidIP(ipAdr) || !isValidIP(subnet)) {
            System.out.println("Ungültige IP oder Subnetz!");
            System.exit(0);
        }

        String[] splitIp = ipAdr.split("\\.");
        String[] splitSubnet = subnet.split("\\.");

        boolean[] ipAdrBin = new boolean[32];
        boolean[] ipSubnetBin = new boolean[32];

        StringBuilder binarySubnet = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            String binIp = decToBinary(Integer.parseInt(splitIp[i]));
            String binSubnet = decToBinary(Integer.parseInt(splitSubnet[i]));
            binarySubnet.append(binSubnet).append(" ");
            System.arraycopy(stringToBin(binIp), 0, ipAdrBin, i * 8, 8);
            System.arraycopy(stringToBin(binSubnet), 0, ipSubnetBin, i * 8, 8);
        }

        String netzID = calculateAddress(ipAdrBin, ipSubnetBin, true);
        String broadcastID = calculateAddress(ipAdrBin, ipSubnetBin, false);
        int UsableClientsint = calculateUsableClients(ipSubnetBin);

        System.out.println("SUBNET-ID-BINARY:    " + binarySubnet.toString().trim());
        System.out.println("NETZ-ID-BINARY:      " + booleanArrayToBinary(ipAdrBin, ipSubnetBin, true));
        System.out.println("BROADCAST-ID-BINARY: " + booleanArrayToBinary(ipAdrBin, ipSubnetBin, false));
        System.out.println("");
        System.out.println("NETZ-ID:        " + netzID);
        System.out.println("Broadcast-ID:   " + broadcastID);
        System.out.println("SUBNET-IP:      " + subnet);
        System.out.println("USABLE-CLIENTS: " + UsableClientsint);
    }

    // Calculate Network ID (AND) or Broadcast Address (OR with Inverted Subnet Mask)
    static String calculateAddress(boolean[] ipAdr, boolean[] ipSubnet, boolean isNetworkID) {
        StringBuilder resultBinary = new StringBuilder();

        for (int i = 0; i < ipAdr.length; i++) {
            if (isNetworkID) {
                resultBinary.append(ipAdr[i] & ipSubnet[i] ? "1" : "0"); // Bitwise AND
            } else {
                resultBinary.append(ipAdr[i] | !ipSubnet[i] ? "1" : "0"); // Bitwise OR with NOT subnet
            }
        }

        return binaryToIP(resultBinary.toString());
    }

    static int calculateUsableClients(boolean[] ipSubnet) {
        int zerosInBinary = 0;
        for (int i = 0; i < ipSubnet.length; i++) {
            if (!ipSubnet[i]) {
                zerosInBinary++;
            }
        }
        double result = Math.pow(2, zerosInBinary);
        result = result -2;
        return (int) result;
    }

    // Convert binary string to IP format
    static String binaryToIP(String binary) {
        String[] octets = binary.split("(?<=\\G.{8})");
        StringBuilder ip = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            ip.append(Integer.parseInt(octets[i], 2));
            if (i < 3) ip.append(".");
        }

        return ip.toString();
    }

    // Convert decimal to 8-bit binary string
    static String decToBinary(int n) {
        return String.format("%8s", Integer.toBinaryString(n)).replace(' ', '0');
    }

    // Convert binary string to boolean array
    static boolean[] stringToBin(String binString) {
        boolean[] binArray = new boolean[binString.length()];
        for (int i = 0; i < binString.length(); i++) {
            binArray[i] = binString.charAt(i) == '1';
        }
        return binArray;
    }

    // IP Validation (IP in Range?)
    static boolean isValidIP(String ip) {
        String regex = "^((25[0-5]|2[0-4][0-9]|1?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|1?[0-9][0-9]?)$";
        return Pattern.matches(regex, ip);
    }

    static String booleanArrayToBinary(boolean[] ipAdr, boolean[] ipSubnet, boolean isNetworkID) {
        StringBuilder resultBinary = new StringBuilder();
        for (int i = 0; i < ipAdr.length; i++) {
            if (isNetworkID) {
                resultBinary.append(ipAdr[i] & ipSubnet[i] ? "1" : "0");
            } else {
                resultBinary.append(ipAdr[i] | !ipSubnet[i] ? "1" : "0");
            }
            if ((i + 1) % 8 == 0 && i != 31) {
                resultBinary.append(" ");
            }
        }
        return resultBinary.toString();
    }
}
