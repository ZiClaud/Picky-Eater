package pickyeater.UI.registerpage;

/**
 * @author Claudio Di Maio
 */
import pickyeater.UI.app.dailyprogresspage.DailyProgressPage;
import pickyeater.algorithms.NutrientsRequiremenetCalculatorWrong;
import pickyeater.algorithms.NutrientsRequirementCalculator;
import pickyeater.basics.food.Nutrients;
import pickyeater.builders.NutrientsBuilder;
import pickyeater.builders.PickyNutrientsBuilder;
import pickyeater.builders.UserBuilder;
import pickyeater.database.*;
import pickyeater.executors.ExecutorProvider;
import pickyeater.executors.RegisterExecutor;
import pickyeater.managers.EaterManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Register4 extends JFrame {
    private JPanel mainPanel;
    private JButton btBack;
    private JButton btDone;
    private JLabel tfCalories;
    private JTextField tfProteins;
    private JTextField tfCarbs;
    private JTextField tfFats;
    private JButton btReset;
    NutrientsBuilder nutrientsBuilder = new PickyNutrientsBuilder();

    public Register4(EaterManager eaterManager, ExecutorProvider executorProvider, UserBuilder userBuilder) {
        setContentPane(mainPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        ResetNutrients(executorProvider.getRegisterExecutor());

        btBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
                new Register3(eaterManager, executorProvider, userBuilder);
            }
        });

        btDone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                JOptionPane.showMessageDialog(mainPanel, "Selected:"  + "\n" + "Calories: " + nutrientsBuilder.getCalories() + "\n" + "Proteins: " + nutrientsBuilder.getProteins() + "\n" + "Carbs: " + nutrientsBuilder.getCarbs() + "\n" + "Fats: " + nutrientsBuilder.getFats());

                userBuilder.setRequiredNutrients(nutrientsBuilder.build());
                executorProvider.getRegisterExecutor().saveUser(userBuilder.build());

                setVisible(false);
                // Todo -> Togli sto coso, devi portarti gli executors, non i database
                PickyEatersDatabase databases = new SQLPickyEaterDB("PickyEatersDB.sqlite");
                new DailyProgressPage(databases);
            }
        });

        btReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ResetNutrients(executorProvider.getRegisterExecutor());
            }
        });

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (Double.parseDouble(tfProteins.getText()) > 5000 | Double.parseDouble(tfProteins.getText()) < 0){
                    JOptionPane.showMessageDialog(mainPanel, "Insert valid number in Proteins", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    nutrientsBuilder.setProteins(Float.parseFloat(tfProteins.getText()));
                }

                if (Double.parseDouble(tfFats.getText()) > 5000 | Double.parseDouble(tfFats.getText()) < 0){
                    JOptionPane.showMessageDialog(mainPanel, "Insert valid number in Fats", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    nutrientsBuilder.setUnSaturatedFats(Float.parseFloat(tfFats.getText()));
                }

                if (Float.parseFloat(tfCarbs.getText()) > 5000 | Float.parseFloat(tfCarbs.getText()) < 0){
                    JOptionPane.showMessageDialog(mainPanel, "Insert valid number in Carbs", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    nutrientsBuilder.setComplexCarbs(Float.parseFloat(tfCarbs.getText()));
                }

                tfCalories.setText(Double.toString(nutrientsBuilder.getCalories()));
            }
        };
        tfFats.addActionListener(listener);
        tfCarbs.addActionListener(listener);
        tfProteins.addActionListener(listener);
    }

    private void ResetNutrients(RegisterExecutor registerExecutor){
        // Get nutrients from NutrientsRequirementCalculator
        NutrientsRequirementCalculator nutrientsCalculated = new NutrientsRequiremenetCalculatorWrong();
        Nutrients nutrients = nutrientsCalculated.calculate(registerExecutor.getUserBuilder().getHeight(), registerExecutor.getUserBuilder().getWeight(), registerExecutor.getUserBuilder().getSex(), registerExecutor.getUserBuilder().getLifeStyle());

        // Do stuff to text fields (tf)
        tfCalories.setText(Double.toString(nutrients.getCalories()));
        tfProteins.setText(Double.toString(nutrients.getProteins()));
        tfCarbs.setText(Double.toString(nutrients.getCarbs()));
        tfFats.setText(Double.toString(nutrients.getFats()));

        // Refresh monitor
        setVisible(true);

        // save also to newNutrientsTmp
        nutrientsBuilder.setComplexCarbs(Float.parseFloat(tfCarbs.getText()));
        nutrientsBuilder.setUnSaturatedFats(Float.parseFloat(tfFats.getText()));
        nutrientsBuilder.setProteins(Float.parseFloat(tfProteins.getText()));
    }

}