import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class SurveySystem {
    private JFrame mainFrame;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private ArrayList<Question> questions;
    private ArrayList<String> responses;
    private int currentQuestionIndex;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SurveySystem survey = new SurveySystem();
            survey.initializeUI();
        });
    }
    
    public SurveySystem() {
        questions = new ArrayList<>();
        responses = new ArrayList<>();
        currentQuestionIndex = 0;
        
        questions.add(new Question("How satisfied are you with our service?", 
                     new String[]{"Very Satisfied", "Satisfied", "Neutral", "Dissatisfied", "Very Dissatisfied"}));
        questions.add(new Question("How likely are you to recommend us to a friend?", 
                     new String[]{"Very Likely", "Likely", "Neutral", "Unlikely", "Very Unlikely"}));
        questions.add(new Question("How would you rate the user experience?", 
                     new String[]{"Excellent", "Good", "Average", "Below Average", "Poor"}));
    }
    
    private void initializeUI() {
        // Setup main frame
        mainFrame = new JFrame("Simple Survey System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 400);
        mainFrame.setLocationRelativeTo(null);
        
        // Card layout to switch between questions
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // Welcome panel
        JPanel welcomePanel = createWelcomePanel();
        cardPanel.add(welcomePanel, "Welcome");
        
        // Create panels for each question
        for (int i = 0; i < questions.size(); i++) {
            JPanel questionPanel = createQuestionPanel(questions.get(i), i);
            cardPanel.add(questionPanel, "Question " + i);
        }
        
        // Thank you panel
        JPanel thankYouPanel = createThankYouPanel();
        cardPanel.add(thankYouPanel, "ThankYou");
        
        mainFrame.add(cardPanel);
        mainFrame.setVisible(true);
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255));
        
        JLabel titleLabel = new JLabel("Welcome to our Survey");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel subtitleLabel = new JLabel("Your feedback is important to us");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JButton startButton = new JButton("Start Survey");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(70, 130, 180));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        
        startButton.addActionListener(e -> cardLayout.show(cardPanel, "Question 0"));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(subtitleLabel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createQuestionPanel(Question question, int questionIndex) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255));
        
        JLabel questionLabel = new JLabel("Question " + (questionIndex + 1) + ": " + question.getText());
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        optionsPanel.setOpaque(false);
        
        ButtonGroup buttonGroup = new ButtonGroup();
        
        for (String option : question.getOptions()) {
            JRadioButton radioButton = new JRadioButton(option);
            radioButton.setFont(new Font("Arial", Font.PLAIN, 14));
            radioButton.setOpaque(false);
            buttonGroup.add(radioButton);
            optionsPanel.add(radioButton);
        }
        
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navigationPanel.setOpaque(false);
        
        JButton previousButton = new JButton("Previous");
        previousButton.setEnabled(questionIndex > 0);
        previousButton.addActionListener(e -> {
            cardLayout.show(cardPanel, questionIndex > 0 ? "Question " + (questionIndex - 1) : "Welcome");
        });
        
        JButton nextButton = new JButton("Next");
        nextButton.setBackground(new Color(70, 130, 180));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(e -> {
            // Get selected response
            for (Component comp : optionsPanel.getComponents()) {
                if (comp instanceof JRadioButton) {
                    JRadioButton radioButton = (JRadioButton) comp;
                    if (radioButton.isSelected()) {
                        if (responses.size() > questionIndex) {
                            responses.set(questionIndex, radioButton.getText());
                        } else {
                            responses.add(radioButton.getText());
                        }
                        break;
                    }
                }
            }
            
            if (questionIndex < questions.size() - 1) {
                cardLayout.show(cardPanel, "Question " + (questionIndex + 1));
            } else {
                saveResponses();
                cardLayout.show(cardPanel, "ThankYou");
            }
        });
        
        navigationPanel.add(previousButton);
        navigationPanel.add(nextButton);
        
        panel.add(questionLabel, BorderLayout.NORTH);
        panel.add(optionsPanel, BorderLayout.CENTER);
        panel.add(navigationPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createThankYouPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255));
        
        JLabel titleLabel = new JLabel("Thank You!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel messageLabel = new JLabel("Your responses have been recorded.");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JButton closeButton = new JButton("Close Survey");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(new Color(70, 130, 180));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> mainFrame.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void saveResponses() {
        try {
            File file = new File("survey_responses.txt");
            FileWriter fw = new FileWriter(file, true);
            PrintWriter pw = new PrintWriter(fw);
            
            pw.println("Survey Response - " + new Date());
            for (int i = 0; i < questions.size(); i++) {
                String response = i < responses.size() ? responses.get(i) : "No response";
                pw.println(questions.get(i).getText() + ": " + response);
            }
            pw.println("------------------------------");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, 
                "Failed to save responses", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static class Question {
        private String text;
        private String[] options;
        
        public Question(String text, String[] options) {
            this.text = text;
            this.options = options;
        }
        
        public String getText() {
            return text;
        }
        
        public String[] getOptions() {
            return options;
        }
    }
}