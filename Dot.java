package gol;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;

/**
 * 
 * @author Sidney Ribeiro Júnior <srjsoftware@gmail.com>
 *
 */
public class Dot extends Thread {
	
	private int x, y;
	private Plane plane;

	public Dot(Plane plane, int x, int y) {
		this.x = x;
		this.y = y;
		this.plane = plane;
	}
	
	@Override
	public void run() {
		int neighbors;
		int[] nbArray;
		
		while (true) {
			try {
				plane.barrier.await();
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				return;
			} catch (BrokenBarrierException ex) {
		        return;
		    }
			
			plane.printPlane();
			
			neighbors = plane.getAliveNeighbors(x, y) - 1;
			ArrayList<Dot> list = plane.getDeadNeighbors(x, y);
			nbArray = new int[list.size()];
			
			for (int i = 0; i < list.size(); i++)
				nbArray[i] = plane.getAliveNeighbors(list.get(i).getX(), list.get(i).getY());
			
			// Atualiza o número de threads vivas
			if (neighbors > 3 || neighbors < 2)
				plane.decrementThreads();
			for (int i = 0; i < list.size(); i++)
				if (nbArray[i] == 3)
					plane.incrementThreads();
						
			try {
				plane.barrier.await();
			} catch (InterruptedException ex) {
				return;
			} catch (BrokenBarrierException ex) {
		        return;
		    }
			
			plane.updateBarrier();
						
			// Cria os novos pontos (inicia as threads)
			for (int i = 0; i < list.size(); i++)
				if (nbArray[i] == 3)
					plane.insertDot(list.get(i));
			
			// Morte por isolamento ou  inanição
			if (neighbors > 3 || neighbors < 2) {
				plane.setNull(this);
				return;
			}
			
			plane.setPrintMutexTrue();
		}
	}
	
	/**
	 * Retorna a coordenada y do ponto na matriz/plano cartesiano
	 * @return coordenada y
	 */
	public int getX() {
		return this.x;
	}
	
	/**
	 * Retorna a coordenada y do ponto na matriz/plano cartesiano
	 * @return coordenada y
	 */
	public int getY() {
		return this.y;
	}

}
