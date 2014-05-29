package gol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class Plane {
	Dot mThread[][];
	public CyclicBarrier barrier;
	private int aliveThreads, newThreads = 0, generation;
	private boolean printMutex;
	private List<Dot> list;
	
	public Plane(int inicialThreads) {
		mThread = new Dot[100][100];
		aliveThreads = inicialThreads;
		barrier = new CyclicBarrier(aliveThreads);
		list = new ArrayList<Dot>();
		printMutex = true;
		generation = 0;
	}
	
	
	/**
	 * Insere uma nova thread Dot no plano
	 * @param coordenada x
	 * @param coordenada y
	 */
	public synchronized void insertDot(int x, int y) {
		int i;
		for (i = 0; i < list.size(); i++)
			if (list.get(i).getX() >= x && list.get(i).getY() > y) {
				if (list.get(i).getX() == x && list.get(i).getY() == y)
					return;
				break;
			}		
		list.add(i, new Dot(this, x, y));
		list.get(i).start();
	}
	
	
	/**
	 * Insere uma nova thread Dot no plano
	 * @param objeto Dot (novo ponto já instanciado)
	 */
	public synchronized void insertDot(Dot d) {
		int i;
		for (i = 0; i < list.size(); i++) {
			if (list.get(i).getX() >= d.getX() && list.get(i).getY() >= d.getY()) {
				if (list.get(i).getX() == d.getX() && list.get(i).getY() == d.getY())
					return;
				break;
			}
		}
		list.add(i, d);
		list.get(i).start();
	}		
	
	
	/**
	 * Acessa o ponto nas coordenadas x e y
	 * @param coordenada x
	 * @param coordenada y
	 * @return o objeto Dot com as coordenadas x e y
	 */
	private Dot getDot(int x, int y) {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).getX() == x && list.get(i).getY() == y)
				return list.get(i);
		return null;
	}
	
	
	/**
	 * Verifica o número de vizinhos vivos do ponto passado como parâmetro (contando com o ponto passado)
	 * @param coordenada x
	 * @param coordenada y
	 * @return número de vizinhos vivos
	 */
	public int getAliveNeighbors(int x, int y) {
		int cont = 0;
		
		for (int i=-1;i < 2; i++)
			for (int j =-1; j < 2; j++)
				if (getDot(x + i, y + j) != null) // TODO get
					cont++;
		return cont;
	}
	
	
	/**
	 * Verifica o número de vizinhos mortos (posições vazias) ao redor do ponto (x,y)
	 * @param coordenada x
	 * @param coordenada y
	 * @return lista de pontos vazios (vizinhos mortos) 
	 */
	public ArrayList<Dot> getDeadNeighbors(int x, int y) {
		ArrayList<Dot> deadlist = new ArrayList<Dot>();
		for (int i=-1;i < 2; i++)
			for (int j =-1; j < 2; j++)
				if (getDot(x + i, y + j) == null)
					deadlist.add(new Dot(this, x + i, y + j));
		return deadlist;
	}
	
	
	/**
	 * Incrementa o número de threads vivas quando uma nova thread é criada
	 */
	public synchronized void incrementThreads() {
		newThreads++;
	}
	
	
	/**
	 * Decrementa o número de threads vivas quando uma thread termina a execução (morre por isolamento ou inanição)
	 */
	public synchronized void decrementThreads() {
		aliveThreads--;
	}
	
	
	/**
	 * Atualiza o número de threads necessárias para sair de uma barreira
	 */
	public synchronized void updateBarrier() {
		aliveThreads += newThreads/3;
		
		if (aliveThreads != barrier.getParties()) {
			if (aliveThreads > 0)
				barrier = new CyclicBarrier(aliveThreads);
			else 
				return;
		}
		newThreads = 0;
	}
		
	
	/**
	 * Retira uma thread do plano (lista de threads)
	 * @param coordenada x
	 * @param coordenada y
	 */
	public synchronized void setNull(Dot d) {
		list.remove(d);
		// Caso seja a última thread viva
		if (aliveThreads + newThreads/3 == 0) {
			if (Thread.activeCount() == 2) {
				setPrintMutexTrue();
				printPlane();
			}
		}
	}
	
	
	/**
	 * Imprime o plano 2D de x|y = 0 até x|y = 11
	 */
	public synchronized void printPlane() {
		if (printMutex) {
			System.out.println("Geração " + (generation++) + ":");
			for (int j = 10; j >= 0; j--) {
				for (int i = 0; i < 11; i++) {
					if (getDot(i, j) == null)
						System.out.print("\u25A1 ");
					else
						System.out.print("\u25A0 ");
				}
				System.out.println();
			}
			printMutex = false;
		}
	}
	
	/**
	 * Libera a impressão na tela
	 */
	public synchronized void setPrintMutexTrue() {
		printMutex = true;
	}
}
