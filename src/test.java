import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class test {

	public static void main(String[] args) throws IOException,
									InterruptedException {


		Path faxFolder = Paths.get("./demo/");
		WatchService watchService = FileSystems.getDefault().newWatchService();
		faxFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

		//Program has to continuously monitor the folder and if any new
		//unprocessed file found then it will start processing of the file.
		File f = new File("./demo1/","demo.csv");

		WatchKey watchKey;
		do {
			watchKey= watchService.take();
			FileWriter fw= new FileWriter(f);
			for (WatchEvent event : watchKey.pollEvents()) {

				if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
					String fileName = event.context().toString();
					//System.out.println("File Created:" + fileName);

					Optional<String> ot= getExtensionByStringHandling(fileName);
					if(ot.isPresent() && ot.get().equalsIgnoreCase("csv")){
						//	Read the data from the file and convert it into expected CSV file and write result file in another folder.
						FileReader fr = new FileReader("./demo/"+fileName);
						BufferedReader br = new BufferedReader(fr);

						String st;
						HashMap<String,String> map = new HashMap<String, String>();
						while ((st = br.readLine()) != null) {
							fw.write(st);
							fw.write("\n");
							String str[] = st.split(",");
								map.put(str[0], str[1]);
						}
						String max = map.values().stream().max(Comparator.naturalOrder()).get();

						System.out.println(max);
						fw.write("\n");
						fw.write("Footer");
						fw.write("\n");
						fw.write("Second Higest Salary is :"+max);
				//		fw=	addFooter("./demo1/demo.csv",fw);
						fw.close();
					}
				}
			}
		} while (watchKey.reset());

	}

	public static FileWriter addFooter(String fileName, FileWriter fw)throws IOException{
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);


		while (br.readLine()!= null) {
			fw.write("Footer");
		}
		return fw;
	}
	public static Optional<String> getExtensionByStringHandling(String filename) {
		return Optional.ofNullable(filename)
										.filter(f -> f.contains("."))
										.map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}
}
