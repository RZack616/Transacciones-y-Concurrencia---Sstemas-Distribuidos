package Concurrencias;



import java.time.Duration;
import java.time.Instant;//Esta en segundos, funciona para obtener un instante del proceso que está realizando hasta cierto tiempo.
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;//Es un framework que se encarga para crear hilos para usar: Usa un Callable o Runable 
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class    Concurre {
	
	private static final Instant INICIO = Instant.now(); //Te dice el tiempo de ese preciso instante en segundos (situación actual)
	private static int contadorTareas = 1;

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
                //Viene en el paquete concurrent, permite ejecutar tareas simultaneas con distintos hilos.
		List<Callable<String>> tareas=  //El Callable es cuando recien se inicia la tarea 
				Stream.generate(Concurre::getTareaSleepUnSegundo)
					.limit(5) //Tiempo de respuesta
					.collect(Collectors.toList()); //Lo que regrese lo convierte en una lista
		
		ExecutorService executor = Executors.newFixedThreadPool(2); //Abrir el número de hilos que se usaran
		
		List<Future<String>> futures = executor.invokeAll(tareas); //Future representa un futuro resultado de un calculo asíncrono y el resultado aparecerá después de que el procesamiento se haya completado
		for(Future<String> future : futures) { //Siempre crea un resultado nuevo cada que se mete un future.get 
			String resultado= future.get(); //Mete los resultados de las tareas que tenemos 
			Log(resultado); //Imprime el resultado.
		}
		
		Log("El hilo principal continúa..."); //Solo imprima ese mensaje con los hilos
		
		String resultadoAny = executor.invokeAny(tareas); //Para ejecutar el invoke any, el primero que me llega es lo que muestra, sino llega entonces mata las demás tareas
		Log(resultadoAny); //Manda a imprimir 
		
		Log("El hilo principal continúa..."); //Solo una impresión
		executor.shutdown(); //Era para apagar el executor 
	}
	
        //Crea un int en númerop de tarea y ese mismo le suma el número de tareas.
	private static Callable<String> getTareaSleepUnSegundo() {
		int numeroTarea = contadorTareas++; //Contador
		
		return ()->{
			Log("Inicio de la tarea " + numeroTarea); //Log es una clase que recibe un mensaje con el número de tarea en String
			try {
				TimeUnit.SECONDS.sleep(1); //Temporizador para vida de una tarea
				Log("Finaliza la tarea " + numeroTarea); //Enviar un string al método log con ese mensaje
				return "resultado de la tarea " + numeroTarea; //Se muestra el resultado 
			} catch (InterruptedException e) {
				Log("sleep ha sido interrumpido en tarea " + numeroTarea);
				return null; //Toma una excepción y no regresa nada, tarea interrumpidas que se estaban haciendo para el método invokeAny
			}
		};
	}
	
        //Es un método que recibe un mensaje e imprime un mensaje en cierto mensaje programado
	private static void Log(Object mensaje) {
		System.out.println(String.format("%s [%s] %s",  //SUPOSICIÓN: String, Cadena de String, String
			Duration.between(INICIO, Instant.now()), Thread.currentThread().getName(), mensaje.toString()));
                        /*
                        Duration.between Imprime la duración del inicio del programa y el instante en el que se está imprimiendo.
                        Despúés imprime el nombre del hilo que invocó al log y posteriormente el mensaje correspondiente de ese instante
                        */
                
                
	}
}

/* ExecutorServiceInvokeAllAny Output
	--invokeAll
	PT0.084S [pool-1-thread-2] Inicio de la tarea 2
	PT0.084S [pool-1-thread-1] Inicio de la tarea 1
	PT1.116S [pool-1-thread-2] Finaliza la tarea 2
	PT1.116S [pool-1-thread-1] Finaliza la tarea 1
	PT1.116S [pool-1-thread-2] Inicio de la tarea 3
	PT1.116S [pool-1-thread-1] Inicio de la tarea 4
	PT2.12S [pool-1-thread-1] Finaliza la tarea 4
	PT2.12S [pool-1-thread-2] Finaliza la tarea 3
	PT2.12S [pool-1-thread-1] Inicio de la tarea 5
	PT3.123S [pool-1-thread-1] Finaliza la tarea 5
	PT3.123S [main] resultado de la tarea 1
	PT3.125S [main] resultado de la tarea 2
	PT3.126S [main] resultado de la tarea 3
	PT3.126S [main] resultado de la tarea 4
	PT3.126S [main] resultado de la tarea 5
	PT3.127S [main] El hilo principal continúa...
	--invokeAny
	PT3.127S [pool-1-thread-2] Inicio de la tarea 1
	PT3.127S [pool-1-thread-1] Inicio de la tarea 2
	PT4.128S [pool-1-thread-1] Finaliza la tarea 2
	PT4.128S [pool-1-thread-2] Finaliza la tarea 1
	PT4.128S [pool-1-thread-1] Inicio de la tarea 3
	PT4.128S [main] resultado de la tarea 2
	PT4.129S [pool-1-thread-1] sleep ha sido interrumpido en tarea 3
	PT4.129S [main] El hilo principal continúa...
*/