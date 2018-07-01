package chat.figg;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Server extends Thread {

    private static ArrayList<BufferedWriter> clientes;
    private static ServerSocket servidor;
    private String nome;
    private Socket endpoint;
    private BufferedReader leitor;

    public Server(Socket con) {
        this.endpoint = con;
        try {
            InputStream in = con.getInputStream();
            InputStreamReader inr = new InputStreamReader(in);
            leitor = new BufferedReader(inr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String msg;
            OutputStream output = this.endpoint.getOutputStream();
            Writer escritor = new OutputStreamWriter(output);
            BufferedWriter escritorComBuffer = new BufferedWriter(escritor);
            clientes.add(escritorComBuffer);
            nome = msg = leitor.readLine();

            while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
                msg = leitor.readLine();
                enviarATodos(escritorComBuffer, msg);
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarATodos(BufferedWriter escritorSaida, String mensagem) throws IOException {
        for (BufferedWriter escritorCliente : clientes) {
            if (escritorSaida != escritorCliente) {
                escritorCliente.write(nome + ": " + mensagem + "\r\n");
                escritorCliente.flush();
            }
        }
    }

    public static void main(String[] args) {
        try {
            //Cria os objetos necessário para instânciar o servidor
            JLabel lblMessage = new JLabel("Porta do Servidor:");
            JTextField txtPorta = new JTextField("12345");
            Object[] texts = {lblMessage, txtPorta};
            JOptionPane.showMessageDialog(null, texts);
            servidor = new ServerSocket(Integer.parseInt(txtPorta.getText())); //Server socket fica escutando na porta indicada.
            clientes = new ArrayList<>();
            JOptionPane.showMessageDialog(null, "Servidor ativo na porta: "
                    + txtPorta.getText());

            while (true) {
                System.out.println("Aguardando conexão...");
                Socket con = servidor.accept();
                System.out.println("Cliente conectado...");
                Thread t = new Server(con);
                t.start();
            }

        } catch (HeadlessException | IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }// Fim do método main  
}
