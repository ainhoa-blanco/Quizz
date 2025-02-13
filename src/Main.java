import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static final String redColor= "\u001B[31m";
    private static final String greenColor= "\u001B[32m";
    private static final String resetColor= "\u001B[0m";

    public static void main(String[] args) {
        int opcio;
        int numPreguntas;
        boolean seguir=true;
        float aciertos=0;
        String nom = null;
        String respuesta ="";
        String[] preguntas=crearPreguntas();
        String [][]respuestas=crearRespuestas();
        String [] letras= {"A","B","C","D"};
        String [] respuestaCorrecta=respuestasCorrectas();
        int fallos = 0;
        do {
            opcio= menu();
            if (opcio==1){
                nom=demanarUsuari();
                numPreguntas = numeroPreg();
                int[] numerosRandoms = new int[numPreguntas];
                Arrays.fill(numerosRandoms,0,numPreguntas,-1);
                aciertos= comprobar (numPreguntas, preguntas,respuestas,letras,numerosRandoms,respuesta,respuestaCorrecta,aciertos);
                puntuacion(aciertos,numPreguntas);
                fallos = (int) (numPreguntas - aciertos);
            }else {
                seguir=false;
            }
        }while (seguir);

        String preguntesT = "src/resources/preguntes.txt";

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(preguntesT))) {
            for (String pregunta : preguntas){
                bw.write(pregunta);
                bw.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error");
        }

        String respostesT = "src/resources/respostes.txt";

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(respostesT))) {
            for (int i = 0; i < respuestas.length; i++) {
                for (int j = 0; j < respuestas[i].length; j++) {
                    bw.write(respuestas[i][j]);
                    if (j == 3) {
                        bw.write(System.lineSeparator());
                    }else {
                        bw.write(",");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error");
        }

        LocalDateTime dataActual = LocalDateTime.now();
        String dataActualString = dataActual.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        String usuari = generarUsuari(nom, dataActualString, aciertos, fallos );

        Path ruta = Paths.get ("src/resources/usuaris.txt");

        try {
            //no funciona writeString
            Files.write(ruta, (usuari + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND );
        }catch (IOException e){
            System.out.println("Error");
        }
    }

    private static String generarUsuari(String nom, String dataActualString, float aciertos, int fallos) {
        return nom+ "," + dataActualString + "," + (int)aciertos + "," + fallos;
    }

    private static String demanarUsuari() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Nombre: ");
        String nombre=scanner.nextLine();
        return nombre;
    }

    private static void puntuacion(float aciertos, int numPreguntas) {
        float porcantaje = (aciertos / numPreguntas) * 100;
        System.out.println("Se ha terminado el juego");
        if (porcantaje<=50.0){
            System.out.println("Has hecho un: "+redColor+porcantaje+"% "+resetColor);
        }else {
            System.out.println("Has hecho un: "+greenColor+porcantaje+"% "+resetColor);
        }
    }

    private static float comprobar(int numPreguntas, String[] preguntas, String[][] respuestas, String[] letras, int[] numerosRandoms, String respuesta, String[] respuestaCorrecta, float aciertos) {
        Scanner scanner = new Scanner(System.in);
        Random random=new Random();
        boolean repetirPreg;
        boolean valido;
        int numeroRandom;
        int i=0;
        do {
            numeroRandom=random.nextInt(20);
            repetirPreg=buscarNum(numeroRandom,numerosRandoms);
            if (!repetirPreg){
                numerosRandoms[i]=numeroRandom;
                System.out.println();
                System.out.println(preguntas[numeroRandom]);
                for (int k = 0; k < 4; k++) {
                    System.out.print(letras[k] + ".");
                    System.out.println(respuestas[numeroRandom][k]);
                }
                System.out.println();
                valido=false;
                while (!valido){
                    respuesta=scanner.next().toUpperCase();
                    valido=buscarLetra(respuesta);
                }
                if (respuesta.equals(respuestaCorrecta[numeroRandom])){
                    System.out.println(greenColor+"Respuesta Correcta"+resetColor);
                    aciertos++;
                }else {
                    System.out.println(redColor+"Respuesta Incorrecta"+resetColor);
                }
                i++;
            }
        }while (i<numPreguntas);
        return aciertos;
    }
    private static boolean buscarLetra(String respuesta) {
        boolean valido=false;
        if (!respuesta.equalsIgnoreCase("A")&&!respuesta.equalsIgnoreCase("B")&&!respuesta.equalsIgnoreCase("C")&&!respuesta.equalsIgnoreCase("D")){
            System.out.println("Escribe una letra entre la A y la D");
        }else {
            valido=true;
        }
        return valido;
    }
    private static boolean buscarNum(int numeroRandom, int[] numerosRandoms) {
        boolean encontrar=false;
        int i=0;
        while (!encontrar&&i<numerosRandoms.length){
            if (numeroRandom==numerosRandoms[i]){
                encontrar=true;
            }else {
                i++;
            }
        }
        return encontrar;
    }
    private static int numeroPreg() {
        Scanner scanner = new Scanner(System.in);
        int preguntas;
        do {
            System.out.println("Cuantas preguntas quieres responder? (Mínimo 5 y máximo 20)");
            preguntas= scanner.nextInt();
        }while (preguntas<5 || preguntas>20);
        return preguntas;
    }
    private static int menu() {
        Scanner scanner = new Scanner(System.in);
        int opcio;
        System.out.println("Menú: \n1.Jugar \n2.Salir");
        opcio= scanner.nextInt();
        if (opcio==2){
            System.out.println("Gracias por jugar!");
        }
        return opcio;
    }
    private static String[] respuestasCorrectas(){
        String[] respuestaCorrecta= {"A", "C", "A", "A", "C", "A", "B", "B", "A", "D", "B", "A", "C", "A", "A", "B", "B", "C", "B", "C"};
        return respuestaCorrecta;
    }
    private static String[][] crearRespuestas() {
        String fileName = "src/resources/respostes.txt";
        List<String[]> respuestasList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] respuestasArray = line.split(",");
                respuestasList.add(respuestasArray);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Archivo no encontrado");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo");
        }

        return respuestasList.toArray(new String[0][0]);
    }
    private static  String[] crearPreguntas(){
        String fileName = "src/resources/preguntes.txt";
        List<String> preguntasList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                preguntasList.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error");
        } catch (IOException e) {
            System.out.println("Error");
        }

        return preguntasList.toArray(new String[0]);
    }
}




