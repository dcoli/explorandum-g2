package explorandum.f09.g2;

import java.util.LinkedList;

/**
 * Basic queue implementation using LinkedList
 * @author laima
 *
 */
public class Queue<Object> {
	
	private LinkedList<Object> list;

	public Queue() {
		list = new LinkedList<Object>();
	}
	
	public void enqueue(Object o) {
		list.add(o);
	}
	
	public Object dequeue() {
		return list.remove();
	}
	
	public Object peek() {
		return list.peek();
	}
	
	public int size() {
		return list.size();
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public boolean contains(Object o) {
		return list.contains(o);
	}
}
