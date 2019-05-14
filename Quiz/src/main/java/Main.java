import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class Main {

    private static boolean usedFiftyFifty = false;
    private static boolean usedPublicHelp = false;

    public static void main(String[] args) throws FileNotFoundException {

        startQuiz();
    }

    public static void startQuiz() throws FileNotFoundException {

        Random random = new Random();
        boolean fiftyFifty = true;
        List<String> badlyAnsweredQuestionsWithCorrectAnswers = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.println("How many questions You want");
        String howManyQuestions = scanner.nextLine();
        System.out.println("Choose one of below categories");
        int numberOfCategory = 1;
        for (Map.Entry<String, List<Quiz>> stringListEntry : quizMap().entrySet()) {
            System.out.println(numberOfCategory + "-" + stringListEntry.getKey());
            numberOfCategory++;
        }
        List<String> categoryList = new ArrayList<String>();
        String categoryChoose = scanner.nextLine();
        for (Map.Entry<String, List<Quiz>> stringListEntry : quizMap().entrySet()) {
            categoryList.add(stringListEntry.getKey());
        }

        int goodAnswerCounter = 0;
        List<Quiz> quizList = quizMap().get(categoryList.get(Integer.parseInt(categoryChoose) - 1));

        Collections.shuffle(quizList);

        for (int i = 0; i < Integer.parseInt(howManyQuestions); i++) {
            List<String> answers = new ArrayList<String>(quizList.get(i).getAnswers());
            String rightAnswer = quizList.get(i).getAnswers().get(0);
            Collections.shuffle(answers);
            System.out.println("Question is - " + quizList.get(i).getQuestion());

            char numberOfAnswer = 'A';
            for (String answer : answers) {
                System.out.println("Answer " + numberOfAnswer + " - " + answer);
                numberOfAnswer++;
            }

            String yourAnswer;
            boolean chooseFiftyFifty = false;
            boolean choosePublic = false;
            do {
                int asciiNumber;
                boolean isWrongAnswer;

                do {
                    System.out.println("Type your answer");
                    yourAnswer = scanner.nextLine();
                    if (!yourAnswer.equalsIgnoreCase("")) {
                        asciiNumber = yourAnswer.toUpperCase().charAt(0) - 65;
                    } else {
                        asciiNumber = 0;
                        System.out.println("You have to type some answer");
                    }
                    isWrongAnswer = asciiNumber < 0 || asciiNumber >= answers.size();
                    if (answers.size() <= 2) {
                        if (isWrongAnswer) {
                            System.out.println("Your answer have to be A or B");
                        }
                    } else {
                        if (isWrongAnswer) {
                            System.out.println("Your answer have to be A, B, C or D");
                        }
                    }

                    switch (yourAnswer) {
                        case "fifty":
                            fiftyFifty(quizList.get(i), answers);
                            chooseFiftyFifty = true;
                            break;
                        case "public":
                            publicHelp(quizList.get(i), answers);
                            choosePublic = true;
                            break;
                        default:
                            chooseFiftyFifty = false;
                            choosePublic = false;
                    }
                } while (isWrongAnswer || yourAnswer.equalsIgnoreCase(""));
            } while (chooseFiftyFifty || choosePublic);

            char letter = yourAnswer.toUpperCase().charAt(0);
            int choice = letter - 65;

            if (answers.get(choice).equalsIgnoreCase(rightAnswer)) {
                goodAnswerCounter++;
            } else {
                badlyAnsweredQuestionsWithCorrectAnswers.add("Question - \"" + quizList.get(i).getQuestion() + "\" - Right answer is - " + rightAnswer);
            }
        }
        System.out.println("Number of right anwers is " + goodAnswerCounter);
        System.out.println("Look in what questions You have wrong answers:");
        for (String badlyAnsweredQuestionsWithCorrectAnswer : badlyAnsweredQuestionsWithCorrectAnswers) {
            System.out.println(badlyAnsweredQuestionsWithCorrectAnswer);
        }

    }

    public static void fiftyFifty(Quiz quiz, List<String> randomAnswers) {
        if (randomAnswers.size() <= 3) {
            System.err.println("You cant choose fifty fifty when You have only two answers");
            return;
        }
        if (usedFiftyFifty) {
            System.err.println("You already used fifty fifty");
            return;
        }
        Random random = new Random();
        Set<String> fiftyFiftyAnswers = new HashSet<>();
        List<String> answers = quiz.getAnswers();
        String rightAnswer = answers.get(0);
        fiftyFiftyAnswers.add(rightAnswer);
        do {
            fiftyFiftyAnswers.add(answers.get(random.nextInt(answers.size())));
        } while (fiftyFiftyAnswers.size() < 2);
        answers = new ArrayList<>(fiftyFiftyAnswers);
        char numberOfAnswer = 'A';
        List<String> answersInRightOrder = new ArrayList<>();
        for (String fiftyFiftyAnswer : fiftyFiftyAnswers) {
            int indexOfRandomAnswers = randomAnswers.indexOf(fiftyFiftyAnswer);
            String answer = "Answer " + (char) (numberOfAnswer + indexOfRandomAnswers) + " - " + fiftyFiftyAnswer;

            answersInRightOrder.add(answer);
        }
        Collections.sort(answersInRightOrder);
        for (String answerInRightOrder : answersInRightOrder) {
            System.out.println(answerInRightOrder);
        }

        usedFiftyFifty = true;
    }

    public static void publicHelp(Quiz quiz, List<String> randomAnwers) {
        if (usedPublicHelp) {
            System.err.println("You already used public help");
            return;
        }
        Random random = new Random();
        List<String> answers = quiz.getAnswers();
        String rightAnswer = answers.get(0);
        int rightAnswerIndex = randomAnwers.indexOf(rightAnswer);
        char numberOfAnswers = 'A';
        double[] publicProcentage = new double[answers.size()];
        for (int i = 0; i < publicProcentage.length; i++) {
            publicProcentage[i] = random.nextDouble();
        }
        publicProcentage[rightAnswerIndex] += 0.8;
        double percentSum = 0;
        for (double v : publicProcentage) {
            percentSum += v;
        }
        for (int i = 0; i < publicProcentage.length; i++) {
            publicProcentage[i] /= percentSum;
        }

        for (int i = 0; i < randomAnwers.size(); i++) {
            String answer = randomAnwers.get(i);
            NumberFormat numberFormat = new DecimalFormat("%");
            String value = numberFormat.format(publicProcentage[i]);
            System.out.println("Answer " + numberOfAnswers + " - " + answer + " - " + value);
            numberOfAnswers++;
        }
        usedPublicHelp = true;

    }

    public static List<Quiz> listOfQuizOfCategory(File quizCategory) throws FileNotFoundException {
        List<Quiz> listOfQuiz = new ArrayList<Quiz>();

        Scanner scanner = new Scanner(quizCategory);

        while (scanner.hasNext()) {
            String question = scanner.nextLine();
            String numberOfQuestions = scanner.nextLine();
            int numberOfQuestionsInt = Integer.parseInt(numberOfQuestions);
            List<String> listOfAnwers = new ArrayList<String>(numberOfQuestionsInt);

            for (int i = 0; i < numberOfQuestionsInt; i++) {
                listOfAnwers.add(scanner.nextLine());
            }
            listOfQuiz.add(new Quiz(question, listOfAnwers));
        }
        return listOfQuiz;
    }

    public static Map<String, List<Quiz>> quizMap() throws FileNotFoundException {
        Map<String, List<Quiz>> quizMap = new HashMap<String, List<Quiz>>();
        File file = new File("src/main/resources/quiz");
        File[] files = file.listFiles();
        for (File quizFile : files) {
            quizMap.put(quizFile.getName(), listOfQuizOfCategory(quizFile));
        }
        ArrayList<Quiz> allQuestions = new ArrayList<Quiz>();
        for (List<Quiz> value : quizMap.values()) {
            allQuestions.addAll(value);
        }
        quizMap.put("All questions", allQuestions);
        return quizMap;
    }
}
