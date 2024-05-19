package thread.creation.example.throughput;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Throughput = the number of tasks completed in a given period (tasks/time unit)
 * Throughput < N / T (in practice)
 * N = number of threads = numbers of cores
 * Using a Fixed Thread Pool, we maintain constant number of threads and eliminate the need to
 * recreate the threads
 */
public class Main {

	public static void main( String[] args ) throws IOException {
		String text = new String(
				Files.readAllBytes( Paths.get( "resources/war_and_peace.txt" ) ) );
		startServer( text );
	}

	public static void startServer( String text ) throws IOException {
		HttpServer server = HttpServer.create( new InetSocketAddress( 8000 ), 0 );
		server.createContext( "/search", new WordCountHandler( text ) );

		Executor executor = Executors.newFixedThreadPool( 4 );
		server.setExecutor( executor );
		server.start();
	}

	private static class WordCountHandler implements HttpHandler {

		private String text;

		public WordCountHandler( final String text ) {
			this.text = text;
		}

		@Override
		public void handle( final HttpExchange exchange ) throws IOException {
			String query = exchange.getRequestURI().getQuery();
			String[] keys = query.split( "=" );

			String action = keys[0];
			String word = keys[1];

			if ( !action.equals( "word" ) ) {
				exchange.sendResponseHeaders( 400, 0 );
				return;
			}

			long count = countWord( word );

			System.out.println(word);

			byte[] response = Long.toString( count ).getBytes( StandardCharsets.UTF_8 );
			exchange.sendResponseHeaders( 200, response.length );
			try ( OutputStream outputStream = exchange.getResponseBody() ) {
				outputStream.write( response );
			}
		}

		private long countWord( String word ) {
			long count = 0;
			int index = 0;
			while ( index >= 0 ) {
				index = text.indexOf( word, index );
				if (index >= 0) {
					count++;
					index++;
				}
			}
			return count;
		}
	}
}
