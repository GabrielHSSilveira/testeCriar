package testeCriar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

class Piloto {
    private String codigo;
    private String nome;
    private List<Integer> voltas;
    private Duration tempoTotal;

    public Piloto(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
        this.voltas = new ArrayList<>();
        this.tempoTotal = Duration.ZERO;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public List<Integer> getVoltas() {
        return voltas;
    }

    public Duration getTempoTotal() {
        return tempoTotal;
    }

    public void adicionarVolta(int volta) {
        voltas.add(volta);
    }

    public void adicionarTempo(String tempoVolta) {
        tempoVolta = tempoVolta.replace(",", ".");
        LocalTime tempoVoltaParsed = LocalTime.parse("00:" + tempoVolta);
        tempoTotal = tempoTotal.plus(Duration.between(LocalTime.MIN, tempoVoltaParsed));
    }
}

public class Corrida {
    public static void main(String[] args) {
        String logFile = "C:\\Users\\Biel\\eclipse-workspace\\testeCriar\\src\\testeCriar\\corrida.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            Map<String, Piloto> pilotos = new HashMap<>();

            // Ignorar a primeira linha
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\s+");

                String hora = data[0];
                String[] pilotoData = line.split("–");
                String codigo = pilotoData[0].split("\\s+")[1].trim();
                String nome = pilotoData[1].split("\\s+")[0].trim();


                int volta;
                try {
                    volta = Integer.parseInt(data[3]);
                } catch (NumberFormatException e) {
                    // Tratar a linha inválida, por exemplo, pulando para a próxima iteração do loop
                    continue;
                }

                String tempoVolta = data[4];

                Piloto piloto;
                if (pilotos.containsKey(codigo)) {
                    piloto = pilotos.get(codigo);
                } else {
                    piloto = new Piloto(codigo, nome);
                    pilotos.put(codigo, piloto);
                }

                piloto.adicionarVolta(volta);
                piloto.adicionarTempo(tempoVolta);
            }

            List<Piloto> pilotosClassificados = new ArrayList<>(pilotos.values());
            pilotosClassificados.sort(Comparator.comparingInt((Piloto p) -> p.getVoltas().size()).reversed());

            Collections.reverse(pilotosClassificados);

            // Imprimir o resultado da corrida
            System.out.println("Resultado da Corrida:");
            System.out.println("Posição Chegada | Código Piloto | Nome Piloto | Qtde Voltas Completadas | Tempo Total de Prova");
            for (int i = 0; i < pilotosClassificados.size(); i++) {
                Piloto piloto = pilotosClassificados.get(i);
                int posicaoChegada = i + 1;
                String codigoPiloto = piloto.getCodigo();
                String nomePiloto = piloto.getNome();
                int qtdeVoltasCompletadas = piloto.getVoltas().size();
                Duration tempoTotalProva = piloto.getTempoTotal();

                System.out.printf("%-16s | %-14s | %-11s | %-22s | %02d:%02d:%02d.%03d\n", posicaoChegada, codigoPiloto, nomePiloto,
                        qtdeVoltasCompletadas, tempoTotalProva.toHours(), tempoTotalProva.toMinutes() % 60,
                        tempoTotalProva.getSeconds() % 60, tempoTotalProva.toMillis() % 1000);
                System.out.flush();



            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

