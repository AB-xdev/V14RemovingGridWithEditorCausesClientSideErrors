package vbt;

import org.apache.meecrowave.Meecrowave;


public class MainStarter
{
	private MainStarter()
	{
		
	}
	
	// Program can be accessed under localhost:<angegebenerHttpPort> (here: 8080)
	// Must be stopped manually
	// NOTE: Duplicate instance (on same port) show errors
	@SuppressWarnings("resource")
	public static void main(final String[] args)
	{
		// @formatter:off
		new Meecrowave(new Meecrowave.Builder()
		{
			{
				setHttpPort(8080);
				setTomcatScanning(true);
				setTomcatAutoSetup(false);
				setHttp2(true);
				setTempDir("target/meecrowave/" + System.nanoTime());
				setUseShutdownHook(true);
			}
		})
		.bake()
		.await();
		// @formatter:on
	}
}
