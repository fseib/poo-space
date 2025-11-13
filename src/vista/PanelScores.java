package vista;


import java.awt.*;
import javax.swing.*;
import modelo.Ranking;
import modelo.RegistroJugador;

public class PanelScores extends JPanel {

	private static final long serialVersionUID = 3846298706410544134L;

	public PanelScores() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 600)); 
    }

    public void displayScores() {
        removeAll(); 
        
        Ranking ranking = new Ranking();
        java.util.List<RegistroJugador> topScores = ranking.getRegistros();
        
        JLabel title = new JLabel("TOP 10 HISTÓRICO", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 30));
        title.setForeground(Color.YELLOW);
        add(title, BorderLayout.NORTH);
        
        JPanel scoreList = new JPanel();
        scoreList.setLayout(new BoxLayout(scoreList, BoxLayout.Y_AXIS));
        scoreList.setBackground(Color.BLACK);
        
        scoreList.add(createScoreRow("#", "NOMBRE", "PUNTAJE", Color.CYAN));
        
        for (int i = 0; i < Math.min(10, topScores.size()); i++) {
            RegistroJugador reg = topScores.get(i);
            Color rowColor = (i % 2 == 0) ? Color.GREEN : Color.LIGHT_GRAY;
            scoreList.add(createScoreRow(
                String.valueOf(i + 1), 
                reg.getNombre(), 
                String.valueOf(reg.getPuntaje()), 
                rowColor
            ));
        }
        
        JScrollPane scrollPane = new JScrollPane(scoreList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(Color.BLACK);
        add(scrollPane, BorderLayout.CENTER);
        
        JButton backButton = new JButton("VOLVER AL MENÚ");
        backButton.addActionListener(e -> {
            ((Ventana) SwingUtilities.getWindowAncestor(this)).showMenuPanel();
        });
        add(backButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }
    
    private JPanel createScoreRow(String rank, String name, String score, Color color) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        row.setBackground(Color.BLACK);
        
        JLabel rankL = new JLabel(String.format("%-4s", rank));
        JLabel nameL = new JLabel(String.format("%-15s", name));
        JLabel scoreL = new JLabel(String.format("%8s", score));
        
        rankL.setForeground(color);
        nameL.setForeground(color);
        scoreL.setForeground(color);
        
        row.add(rankL);
        row.add(nameL);
        row.add(scoreL);
        return row;
    }
}