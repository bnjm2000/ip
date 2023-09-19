package Chatty;

import Chatty.tasks.Deadline;
import Chatty.tasks.Event;
import Chatty.tasks.Task;
import Chatty.tasks.Todo;

import java.io.*;
import java.util.Scanner;
public class Chatty {
    private static final String FILE_PATH = "./data/chatty.txt";
    public static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        Task[] tasks = new Task[100];
        int taskCount = 0;
        int index;
        taskCount = loadTasks(tasks, taskCount);
        Scanner scanner = new Scanner(System.in);
        printWelcomeMessage();
        String input = scanner.nextLine();
        loop:
        while (!input.equalsIgnoreCase("bye")) {
            String[] words = input.split(" ");
            String command = words[0].toLowerCase();
            switch (command) {
                case "list":
                    listAllTasks(taskCount, tasks);
                    input = scanner.nextLine();
                    continue loop;
                case "mark":
                    mark(input, tasks);
                    break;
                case "unmark":
                    unmark(input, tasks);
                    break;
                case "todo":
                    taskCount = createTodo(input, tasks, taskCount);
                    break;
                case "deadline":
                    taskCount = createDeadline(input, tasks, taskCount);
                    break;
                case "event":
                    taskCount = createEvent(input, tasks, taskCount);
                    break;
                default:
                    System.out.println(LINE);
                    System.out.println("Unknown command. Please try again or type \"help\"");
                    System.out.println(LINE);
                    input = scanner.nextLine();
                    continue loop;
            }
            System.out.println("Now you have " + taskCount + " tasks in the list.");
            System.out.println(LINE);
            input = scanner.nextLine();
        }
        System.out.println(LINE);
        saveTasks(tasks, taskCount);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
        scanner.close();
    }

    private static int loadTasks(Task[] tasks, int taskCount) {
        try {
            File file = new File(FILE_PATH);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|"); // Fix the delimiter here

                String type = parts[0].trim();
                boolean isDone = parts[1].trim().equals("1");
                String description = parts[2].trim();

                switch (type) {
                case "T":
                    tasks[taskCount] = new Todo(description);
                    break;
                case "D":
                    String by = parts[3].trim();
                    tasks[taskCount] = new Deadline(description, by);
                    break;
                case "E":
                    String fromTo = parts[3].trim();
                    String[] fromToParts = fromTo.split(" to ");
                    String from = fromToParts[0].trim();
                    String to = fromToParts[1].trim();
                    tasks[taskCount] = new Event(description, from, to);
                    break;
                default:
                    throw new IOException("Invalid data in file: Unknown task type.");
                }

                tasks[taskCount].setIsDone(isDone);
                taskCount++;
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Error loading or creating data file.");
        }
        return taskCount;
    }


    private static void saveTasks(Task[] tasks, int taskCount) {
        try {
            FileWriter writer = new FileWriter(FILE_PATH);
            for (int i = 0; i < taskCount; i++) {
                writer.write(tasks[i].saveFormat() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving data file: " + e.getMessage());
        }
    }
    private static void listAllTasks(int taskCount, Task[] tasks) {
        System.out.println(LINE);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + ". " + tasks[i].getDescription());
        }
        System.out.println(LINE);
    }

    private static void mark(String input, Task[] tasks) {
        int index;
        index = Integer.parseInt(input.substring(5)) - 1;
        tasks[index].mark();
        System.out.println(LINE);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(tasks[index].getDescription());
    }

    private static void unmark(String input, Task[] tasks) {
        int index;
        index = Integer.parseInt(input.substring(7)) - 1;
        tasks[index].unmark();
        System.out.println(LINE);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(tasks[index].getDescription());
    }

    private static int createTodo(String input, Task[] tasks, int taskCount) {
        if (input.length() > 5) {
            String todoDescription = input.substring(5);
            tasks[taskCount] = new Todo(todoDescription);
            taskCount++;
            System.out.println("Got it. I've added this task:");
            System.out.println("[T][ ] " + todoDescription);
        } else{
            System.out.println(LINE + "\n☹ OOPS!!! The description of a todo cannot be empty.");
        }
        return taskCount;
    }

    private static int createDeadline(String input, Task[] tasks, int taskCount) {
        int byIndex = input.indexOf("/by");
        //System.out.println(byIndex);
        if (byIndex != -1 && input.length() > 9 && byIndex != 9) {
            String deadlineDescription = input.substring(9, byIndex).trim();
            String by = input.substring(byIndex + 3).trim();
            tasks[taskCount] = new Deadline(deadlineDescription, by);
            taskCount++;
            System.out.println("Got it. I've added this task:");
            System.out.println("[D][ ] " + deadlineDescription + " (by: " + by + ")");
        } else if (byIndex == -1){
            System.out.println(LINE +"\n☹ OOPS!!! Invalid Deadline format. Please use /by to specify a deadline.");
            System.out.println("E.g. deadline homework /by 20 Aug");
        } else if (byIndex == 9 || input.length() <= 9){
            System.out.println(LINE + "\n☹ OOPS!!! Description of Deadline task needed!");
            System.out.println("E.g. deadline homework /by 20 Aug");
        } else{
            System.out.println(LINE +"\n☹ OOPS!!! Unknown Error adding a deadline. Try Again.");
            System.out.println("E.g. deadline homework /by 20 Aug");
        }
        return taskCount;
    }

    private static int createEvent(String input, Task[] tasks, int taskCount) {
        int fromIndex = input.indexOf("/from");
        int toIndex = input.indexOf("/to");
        //System.out.println(fromIndex);
        //System.out.println(toIndex);
        //System.out.println(input.length());
        if (fromIndex != -1 && toIndex != -1 && input.length() > 6 && fromIndex != 6 && toIndex != fromIndex+6) {
            String eventDescription = input.substring(6, fromIndex).trim();
            String from = input.substring(fromIndex + 5, toIndex).trim();
            String to = input.substring(toIndex + 3).trim();
            tasks[taskCount] = new Event(eventDescription, from, to);
            taskCount++;
            System.out.println("Got it. I've added this task:");
            System.out.println("[E][ ] " + eventDescription + " (from: " + from + " to: " + to + ")");
        } else if (fromIndex == -1 || toIndex == -1 || toIndex == fromIndex+6){
            System.out.println(LINE + "\n☹ OOPS!!! Invalid Event format. Please use /from to specify the start of an event and /to to specify the end.");
            System.out.println("E.g. event project meeting /from 20 Aug 4pm /to 6pm");
        } else if (input.length() <= 6 || fromIndex == 6){
            System.out.println(LINE + "\n☹ OOPS!!! Description of Event task needed!");
            System.out.println("E.g. event project meeting /from 20 Aug 4pm /to 6pm");
        } else{
            System.out.println(LINE +"\n☹ OOPS!!! Unknown Error adding an event. Try Again.");
            System.out.println("E.g. event project meeting /from 20 Aug 4pm /to 6pm");
        }
        return taskCount;
    }

    private static void printWelcomeMessage() {
        System.out.println(LINE);
        System.out.println("Hello! I'm Chatty!");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }
}
