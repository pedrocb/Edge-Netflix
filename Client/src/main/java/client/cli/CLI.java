package client.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.util.Scanner;

public class CLI {
    private boolean running = true;
    private String command;

    public CLI() {
        Scanner scanner = new Scanner(System.in);
        Runnable runnable = null;

        while (running) {
            command = scanner.nextLine();
            if (command.equals("seeder list")) {
                runnable = new ListSeedersCommand();
            } else if (command.startsWith("seeder search ")) {
                String[] keywords = command.replace("seeder search ", "").split( " ");
                runnable = new SearchSeedersCommand(keywords);
            } else {
                System.out.println("Bad usage");
                continue;
            }
            runnable.run();
        }
    }
}
