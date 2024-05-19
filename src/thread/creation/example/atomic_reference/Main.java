package thread.creation.example.atomic_reference;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

/**
 *
 */
public class Main {

	public static void main( String[] args ) {
		String string1 = "abc";
		String string2 = new String(string1);
		String string3 = new String("abc");
		String string4 = "abc";


		System.out.println(string1 == string2);
		System.out.println(string1.equals(string2));
		System.out.println(string1 == string3);
		System.out.println(string2 == string3);
		System.out.println(string1 == string4);
	}

	private static class LockFreeStack<T> {
		private AtomicReference<StackNode<T>> head = new AtomicReference<>();
		private AtomicInteger counter = new AtomicInteger(0);

		public void push( T value ) {
			StackNode<T> newHead = new StackNode<>( value );
			while ( true ) {
				StackNode<T> currentHeadNode = head.get();
				newHead.next = currentHeadNode;
				if ( head.compareAndSet( currentHeadNode, newHead ) ) {
					counter.incrementAndGet();
					break; // successfully push the new value
				} else {
					LockSupport.parkNanos( 1 ); // something bad happened, we try again
				}
			}
		}

		public T pop() {
			StackNode<T> currentHead = head.get();
			StackNode<T> newHead;

			while ( currentHead != null ) {
				newHead = currentHead.next;
				if (head.compareAndSet( currentHead, newHead )) {
					counter.decrementAndGet();
					break;
				} else {
					LockSupport.parkNanos( 1 );
					currentHead = head.get();
				}
			}

			return currentHead != null ? currentHead.value : null;
		}
	}

	private static class StackNode<T> {
		public T value;
		public StackNode<T> next;

		public StackNode( final T value ) {
			this.value = value;
		}
	}
}
