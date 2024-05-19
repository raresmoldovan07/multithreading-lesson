package thread.creation.example.image_processing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * We can get a speed up if we partition a problem into multiple sub-problems
 * More threads than virtual cores is counterproductive
 * There is inherent ost for running an algorithm by multiple threads
 */
public class Main {

	public static final String SOURCE_FILE = "./resources/many-flowers.jpg";
	public static final String DESTINATION_FILE = "./resources/many-flowers-out.jpg";

	public static void main( String[] args ) throws IOException, InterruptedException {
		BufferedImage originalImage = ImageIO.read( new File( SOURCE_FILE ) );
		BufferedImage resultImage = new BufferedImage( originalImage.getWidth(),
				originalImage.getHeight(), BufferedImage.TYPE_INT_RGB );

		recolorImageMultiThreading( originalImage, resultImage, 5 );

		File outputFile = new File( DESTINATION_FILE );
		ImageIO.write( resultImage, "jpg", outputFile );
	}

	public static void recolorImageSingleThread( BufferedImage originalImage,
			BufferedImage resultImage ) {
		recolorImage( originalImage, resultImage, 0, 0, originalImage.getWidth(),
				originalImage.getHeight() );
	}

	public static void recolorImageMultiThreading( BufferedImage originalImage,
			BufferedImage resultImage, int numberOfThreads ) throws InterruptedException {
		List<Thread> threadList = new ArrayList<>();
		int width = originalImage.getWidth();
		int height = originalImage.getHeight() / numberOfThreads;

		for(int i = 0; i < numberOfThreads; ++i) {
			final int threadMultiplier = i;
			Thread thread = new Thread(() -> {
				int leftCorner = 0;
				int topCorner = height * threadMultiplier;

				recolorImage( originalImage, resultImage, leftCorner, topCorner, width, height );
			});
			threadList.add( thread );
		}

		threadList.forEach( Thread::start );
		for ( Thread thread : threadList ) {
			thread.join();
		}
	}

	public static void recolorImage( BufferedImage originalImage, BufferedImage resultImage,
			int leftCorner, int topCorner, int width, int height ) {
		for ( int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); ++x ) {
			for ( int y = topCorner; y < topCorner + height && y < originalImage.getHeight(); ++y ) {
				recolorPixel( originalImage, resultImage, x, y );
			}
		}
	}

	public static void recolorPixel( BufferedImage originalImage, BufferedImage resultImage, int x,
			int y ) {
		int rgb = originalImage.getRGB( x, y );

		int red = getRed( rgb );
		int green = getGreen( rgb );
		int blue = getBlue( rgb );

		int newGreen = green, newRed = red, newBlue = blue;

		if ( isShadeOfGray( red, green, blue ) ) {
			newRed = Math.min( 255, red + 10 );
			newGreen = Math.max( 0, green - 80 );
			newBlue = Math.max( 0, blue - 20 );
		}

		int newRGB = createRGBFromColors( newRed, newGreen, newBlue );
		setRGB( resultImage, x, y, newRGB );
	}

	public static void setRGB( BufferedImage image, int x, int y, int rgb ) {
		image.getRaster()
				.setDataElements( x, y, image.getColorModel().getDataElements( rgb, null ) );
	}

	public static boolean isShadeOfGray( int red, int green, int blue ) {
		return Math.abs( red - green ) < 30 && Math.abs( red - blue ) < 30 && Math.abs(
				green - blue ) < 30;
	}

	public static int createRGBFromColors( int red, int green, int blue ) {
		int rgb = 0;

		rgb |= blue;
		rgb |= green << 8;
		rgb |= red << 16;

		rgb |= 0xFF000000;

		return rgb;
	}

	public static int getGreen( int rgb ) {
		return ( rgb & 0x0000FF00 ) >> 8;
	}

	public static int getRed( int rgb ) {
		return ( rgb & 0x00FF0000 ) >> 16;
	}

	public static int getBlue( int rgb ) {
		return rgb & 0x000000FF;
	}
}
