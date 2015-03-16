package version_7;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

public class EntryAdder extends JPanel implements ActionListener {

	private static final long serialVersionUID = 3666104289045535914L;
	static JLabel label = new JLabel("Please enter a thing:");
	static JPanel panel = new JPanel();
	static GridBagConstraints c = new GridBagConstraints();
//	static JPanel panel2 = new JPanel();
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		panel.setLayout(new GridBagLayout());
		panel.add(new JButton("Do a thing"), c);
		c.gridx = 1;
		c.gridy = 0;
		panel.add(label, c);
		c.gridx = 2;
		c.gridy = 0;
//		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		String[] things = {"entities", "items", "maps"}; 
		JComboBox<String> dropdown = new JComboBox<String>(things);
		dropdown.addActionListener(new EntryAdder());
		panel.add(dropdown, c);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800,500);                                            
		frame.setTitle("Game Thing");                   
		frame.setVisible(true);
		frame.setResizable(false);
		frame.getContentPane().add(panel);
	}

    public EntryAdder() {
        super(new BorderLayout());

    }

    /** Listens to the combo box. */
    public void actionPerformed(ActionEvent e) {
        @SuppressWarnings("unchecked")
		JComboBox<String> cb = (JComboBox<String>) e.getSource();
        String fileName = (String)cb.getSelectedItem();
        try {
			updateLabel(fileName);
		} catch (FileNotFoundException e1) {
			System.out.println("Cannot find file.");
		} catch (IOException e2) {
			System.out.println("Some other error");
		}
    }

    protected void updateLabel(String file) throws IOException {
		String filename = "C:\\Users\\Sam\\eclipse\\workspace\\ASCII_Game\\"+file+".txt";
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		ArrayList<String> contents = new ArrayList<String>();
		
		String line = reader.readLine();
		while (line != null) {
			contents.add(line);
			line = reader.readLine();
		}
		
		reader.close();
		if (file.equals("entities")) {
			//These will be a list of entities that are present, with an option to add a new one
			//ArrayList<EntityData> entityList = new ArrayList<EntityData>();
			//JComboBox<String> entitySelector = new JComboBox<String>();
			c.gridx = 0;
			c.gridy = 1;
			panel.add(new JLabel("Entities: "), c);
			ArrayList<String> entityNames = new ArrayList<String>();
			for (int i=0; i<contents.size(); i++) {
				String str = contents.get(i).trim();
				if (str!="") {
					ArrayList<String> info = new ArrayList<String>();
					info.add(str);
					i++;
					str = contents.get(i).trim();
					while (!str.equals("ent<")) {
						str = contents.get(i).trim();
						info.add(str);
						i++;
					}
					//This will now be a list of data for a single entity
					//Convert it into EntityData form
					EntityData data = new EntityData();
					String name = info.get(0).replace(">", "");
					entityNames.add(name);
					data.setName(name);
					System.out.println("\nName: "+name);
					int entI = 1;
					line = "";
					while (entI<info.size()) {
						line = info.get(entI);
						System.out.println(entI+" "+line);
						//Add everything to data
						entI++;
					}
				}
				//TODO - I need to convert the strings I get into an EntityData, then do all the things
				//below this close } thing.
			}
			/*String tag = contents.get(i).split("> ?(\\d*)$")[0];
			JLabel label2 = new JLabel(tag);
			label2.setHorizontalAlignment(JLabel.RIGHT);
			String value = str.replaceAll(".*> ?(\\d*)$", "$1");
			if (value!="") {
				c.gridx = 0;
				c.gridy = i+1;
				System.out.println(str);
				String text = Character.toUpperCase(tag.charAt(0)) + tag.substring(1);
				label2.setText(text);
				panel.add(label2, c);
				c.gridx = 1;
				c.gridy = i+1;
				panel.add(new JTextField(value));
			}
			else {
				c.gridx = 1;
				c.gridy = i+1;
				System.out.println("Derp");
				label2.setText(Character.toUpperCase(tag.charAt(0)) + tag.substring(1));
				panel.add(label2, c);
			}
			label.setText("Entities:");*/
		} else if (file.equals("items")) {
			contents.set(0, contents.get(0).replace("item: ", ""));
			for (int i=0; i<contents.size(); i++) {
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				System.out.println(i);
				c.gridy = i+1;
				String str = contents.get(i);
				JLabel label2 = new JLabel(str);
				label2.setHorizontalAlignment(JLabel.RIGHT);
				JTextField text = new JTextField(Integer.toString(i), 10);
				panel.add(label2, c);
				c.gridx = 1;
				panel.add(text, c);
				System.out.println(c.gridx+" "+c.gridy);
			}
			label.setText("Items:");
		}
    }
}
