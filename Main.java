package gol;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Sidney Ribeiro Júnior <srjsoftware@gmail.com>
 * 
 * Implementação do Jogo da Vida (Game of Life), desenvolvido por John Horton Conway.
 * Nessa implementação cada ponto é um objeto/thread do tipo Dot responsável por sua própria
 * "vida" e pelo "nascimento" de seus vizinhos.
 * O plano 2D é representado pela classe Plane, que armazenda as threads vivas em uma matriz.
 * Essa classe também é responsável por manter o número de threads ativas para poder controlar
 * as sincronizações nas barreiras.
 *
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int initialThreads = 0;
		Scanner scnr;
		ArrayList<int[]> initT = new ArrayList<int[]>();
		
		if (args.length == 0)
			scnr = new Scanner(System.in);
		else
			try {
				scnr = new Scanner(new FileReader(args[0]));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				System.out.println("Uso: java gol ARQUIVO");
				return;
			}
		
		while (scnr.hasNextInt()) {
			int x[] = {scnr.nextInt(), 0};
			if (scnr.hasNextInt()) {
				x[1] = scnr.nextInt();
			} else {
				System.out.println("Arquivo inválido.");
				scnr.close();
				return;
			}
			initT.add(x);
			initialThreads++;
		}
		
		scnr.close();
		
		if (initialThreads == 0) {
			System.out.println("Nenhuma entrada válida. Dê ENTER após digitar as coordenadas.");
			return;
		}
		
		Plane plane = new Plane(initialThreads);
		for (;initialThreads > 0; initialThreads--) {
			plane.insertDot(initT.get(initialThreads-1)[0],initT.get(initialThreads-1)[1]);
		}
	}

}
