import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class Graphic extends JApplet{
    JLabel label;
    Canvas canvas;
    JFileChooser chooser;
    JTextArea text;
    JComboBox<String> combo;
    ButtonGroup button;

    public Graphic(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e){
            //
        }

        JMenuBar menuBar=new JMenuBar();  //����˵���
        JMenu file=new JMenu("File");
        menuBar.add(file);
        JMenu edit=new JMenu("Edit");
        menuBar.add(edit);
        setJMenuBar(menuBar);

        JMenuItem open=new JMenuItem("Open");  //����Open�˵���
        open.addActionListener(new OpenListener());  //����Open
        file.add(open);

        JMenuItem exit=new JMenuItem("Exit");  //����Exit�˵���
        exit.addActionListener(new ActionListener(){  //����Exit
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
        file.add(exit);

        JMenuItem paint=new JMenuItem("Paint");  //����Paint�˵���
        paint.addActionListener(new ActionListener(){  //����Paint
            public void actionPerformed(ActionEvent e){
                label.setIcon(null);
                text.setText(null);
                canvas=new Canvas();
                canvas.setVisible(false);
                add(canvas,BorderLayout.CENTER);
                canvas.setVisible(true);
            }
        });
        edit.add(paint);

        JMenuItem clear=new JMenuItem("Clear");  //����Clear�˵���
        clear.addActionListener(new ActionListener(){  //����Clear
            public void actionPerformed(ActionEvent e){
                label.setIcon(null);
                text.setText(null);
                canvas.setVisible(false);
            }
        });
        edit.add(clear);

        chooser=new JFileChooser();  //�ļ�����ѡ��
        chooser.setFileFilter(new FileNameExtensionFilter("GIF Image","gif"));

        text=new JTextArea(1,60);  //�ļ�����ʾ��
        text.setEnabled(false);

        combo=new JComboBox<String>();  //������״�����˵�
        combo.addItem("oval");
        combo.addItem("rectangle");
        combo.addItem("triangle");

        button=new ButtonGroup();  //������ɫ��ť��
        JRadioButton red=new JRadioButton("red");
        red.setActionCommand("r");
        button.add(red);
        JRadioButton yel=new JRadioButton("yellow");
        yel.setActionCommand("y");
        button.add(yel);
        JRadioButton gre=new JRadioButton("green");
        gre.setActionCommand("g");
        button.add(gre);
        red.setSelected(true);  //����ɫ��ΪĬ��

        JPanel panel=new JPanel();  //�������
        getContentPane().add(panel,BorderLayout.SOUTH);  //��������ڵײ�
        panel.add(text,BorderLayout.WEST);  //���ļ�����ʾ���������
        panel.add(combo,BorderLayout.CENTER);  //����״�����˵���������
        panel.add(red,BorderLayout.EAST);  //���Ҳ����������ɫ��ť
        panel.add(yel,BorderLayout.EAST);
        panel.add(gre,BorderLayout.EAST);
        setVisible(true);

        label=new JLabel();
        canvas=new Canvas();
    }

    public class OpenListener implements ActionListener{  //Open������
        public void actionPerformed(ActionEvent e){
            if(chooser.showOpenDialog(Graphic.this)==JFileChooser.APPROVE_OPTION){
                String name=chooser.getSelectedFile().getPath();  //��ȡ�ļ���
                text.setText(name);
                ImageIcon icon=new ImageIcon(name);

                int imgWidth=icon.getIconWidth(),imgHeight=icon.getIconHeight();  //ͼƬԭ�ߴ�
                int conWidth=getWidth(),conHeight=getHeight()-100;  //���ڳߴ�
                int Width,Height;
                if(imgWidth<=conWidth&&imgHeight<=conHeight){  //ͼƬ����������ڴ��ڣ���ԭ�ߴ���ʾ
                    Width=imgWidth;
                    Height=imgHeight;
                }
                else if(imgWidth/imgHeight>conWidth/conHeight){  //ͼƬ������С�ߴ�
                    Width=conWidth;
                    Height=imgHeight*conWidth/imgWidth;
                }
                else{  //ͼƬ��������С�ߴ�
                    Height=conHeight;
                    Width=imgWidth*conHeight/imgHeight;
                }

                canvas.setVisible(false);
                canvas=new Canvas();
                icon=new ImageIcon(icon.getImage().getScaledInstance(Width,Height,Image.SCALE_DEFAULT));
                label.setIcon(icon);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                add(label,BorderLayout.CENTER);
                setVisible(true);
            }
        }
    }

    public class Canvas extends JLabel{  //������
        ArrayList<Shape> shapes=new ArrayList<Shape>();  //�洢ͼ��
        ArrayList<Color> colors=new ArrayList<Color>();  //�洢��ɫ
        Point start=null,end=null;
        public Canvas(){
            addMouseListener(new MouseAdapter(){  //��������
                public void mousePressed(MouseEvent e){  //��갴���¼�
                    colors.add(getColor());  //���뵱ǰ��ɫ
                    start=new Point(e.getX(),e.getY());  //��¼���
                    repaint();
                }

                public void mouseReleased(MouseEvent e){  //����ͷ��¼�
                    Shape r=getShape(start.x,start.y,e.getX(),e.getY());  //������״
                    shapes.add(r);  //���뵱ǰͼ��
                    start=end=null;
                    repaint();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter(){  //���������
                public void mouseDragged(MouseEvent e){
                    end=new Point(e.getX(),e.getY());  //��¼�յ�
                    repaint();
                }
            });
        }

        public void paint(Graphics g){  //�����ɫ����
            Graphics2D g2d=(Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);//�����

            if(start!=null&&end!=null){  //�����϶�����״�仯
                Shape s=getShape(start.x,start.y,end.x,end.y);
                g2d.setPaint(colors.get(colors.size()-1));
                g2d.fill(s);
            }

            for(int i=0;i<shapes.size();i++){  //���ζ���״��ɫ
                Shape s=shapes.get(i);
                g2d.setPaint(colors.get(i));
                g2d.fill(s);
            }
        }

        private Color getColor(){  //��ȡ��ɫ����
            String color=button.getSelection().getActionCommand();  //��ȡ��ǰ��ɫ
            if(color.equals("r")){
                return Color.RED;
            }
            else if(color.equals("y")){
                return Color.YELLOW;
            }
            else if(color.equals("g")){
                return Color.GREEN;
            }
            return null;
        }

        private Shape getShape(int x1,int y1,int x2,int y2){  //��ȡ��״����
            String shape=(String)combo.getSelectedItem();  //��ȡ��ǰ��״
            if(shape.equals("oval")){  //������Բ
                return new Ellipse2D.Float(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
            }
            else if(shape.equals("rectangle")){  //���ƾ���
                return new Rectangle2D.Float(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
            }
            else if(shape.equals("triangle")){  //���Ƶ�������
                int x[]={x1,x2,(x1+x2)/2};
                int y[]={Math.max(y1,y2),Math.max(y1,y2),Math.min(y1,y2)};
                return new Polygon(x,y,3);
            }
            return null;
        }
    }

    public static void main(String[] args){  //������
        Graphic applet=new Graphic();
        JFrame frame=new JFrame("Graphic");
        frame.add(applet,BorderLayout.CENTER);
        frame.setSize(800,600);
        applet.init();
        applet.start();
        frame.setVisible(true);
    }
}