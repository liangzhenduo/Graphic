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

        JMenuBar menuBar=new JMenuBar();  //构造菜单栏
        JMenu file=new JMenu("File");
        menuBar.add(file);
        JMenu edit=new JMenu("Edit");
        menuBar.add(edit);
        setJMenuBar(menuBar);

        JMenuItem open=new JMenuItem("Open");  //构造Open菜单项
        open.addActionListener(new OpenListener());  //监听Open
        file.add(open);

        JMenuItem exit=new JMenuItem("Exit");  //构造Exit菜单项
        exit.addActionListener(new ActionListener(){  //监听Exit
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
        file.add(exit);

        JMenuItem paint=new JMenuItem("Paint");  //构造Paint菜单项
        paint.addActionListener(new ActionListener(){  //监听Paint
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

        JMenuItem clear=new JMenuItem("Clear");  //构造Clear菜单项
        clear.addActionListener(new ActionListener(){  //监听Clear
            public void actionPerformed(ActionEvent e){
                label.setIcon(null);
                text.setText(null);
                canvas.setVisible(false);
            }
        });
        edit.add(clear);

        chooser=new JFileChooser();  //文件类型选择
        chooser.setFileFilter(new FileNameExtensionFilter("GIF Image","gif"));

        text=new JTextArea(1,60);  //文件名显示框
        text.setEnabled(false);

        combo=new JComboBox<String>();  //创建形状下拉菜单
        combo.addItem("oval");
        combo.addItem("rectangle");
        combo.addItem("triangle");

        button=new ButtonGroup();  //创建颜色按钮组
        JRadioButton red=new JRadioButton("red");
        red.setActionCommand("r");
        button.add(red);
        JRadioButton yel=new JRadioButton("yellow");
        yel.setActionCommand("y");
        button.add(yel);
        JRadioButton gre=new JRadioButton("green");
        gre.setActionCommand("g");
        button.add(gre);
        red.setSelected(true);  //将红色设为默认

        JPanel panel=new JPanel();  //构造面板
        getContentPane().add(panel,BorderLayout.SOUTH);  //将面板置于底部
        panel.add(text,BorderLayout.WEST);  //将文件名显示框置于左侧
        panel.add(combo,BorderLayout.CENTER);  //将形状下拉菜单置于中央
        panel.add(red,BorderLayout.EAST);  //在右侧依次添加颜色按钮
        panel.add(yel,BorderLayout.EAST);
        panel.add(gre,BorderLayout.EAST);
        setVisible(true);

        label=new JLabel();
        canvas=new Canvas();
    }

    public class OpenListener implements ActionListener{  //Open监听器
        public void actionPerformed(ActionEvent e){
            if(chooser.showOpenDialog(Graphic.this)==JFileChooser.APPROVE_OPTION){
                String name=chooser.getSelectedFile().getPath();  //获取文件名
                text.setText(name);
                ImageIcon icon=new ImageIcon(name);

                int imgWidth=icon.getIconWidth(),imgHeight=icon.getIconHeight();  //图片原尺寸
                int conWidth=getWidth(),conHeight=getHeight()-100;  //窗口尺寸
                int Width,Height;
                if(imgWidth<=conWidth&&imgHeight<=conHeight){  //图片长宽均不大于窗口，按原尺寸显示
                    Width=imgWidth;
                    Height=imgHeight;
                }
                else if(imgWidth/imgHeight>conWidth/conHeight){  //图片过宽，缩小尺寸
                    Width=conWidth;
                    Height=imgHeight*conWidth/imgWidth;
                }
                else{  //图片过长，缩小尺寸
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

    public class Canvas extends JLabel{  //画布类
        ArrayList<Shape> shapes=new ArrayList<Shape>();  //存储图形
        ArrayList<Color> colors=new ArrayList<Color>();  //存储颜色
        Point start=null,end=null;
        public Canvas(){
            addMouseListener(new MouseAdapter(){  //鼠标监听器
                public void mousePressed(MouseEvent e){  //鼠标按下事件
                    colors.add(getColor());  //加入当前颜色
                    start=new Point(e.getX(),e.getY());  //记录起点
                    repaint();
                }

                public void mouseReleased(MouseEvent e){  //鼠标释放事件
                    Shape r=getShape(start.x,start.y,e.getX(),e.getY());  //绘制形状
                    shapes.add(r);  //加入当前图形
                    start=end=null;
                    repaint();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter(){  //鼠标活动监听器
                public void mouseDragged(MouseEvent e){
                    end=new Point(e.getX(),e.getY());  //记录终点
                    repaint();
                }
            });
        }

        public void paint(Graphics g){  //填充颜色方法
            Graphics2D g2d=(Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿

            if(start!=null&&end!=null){  //绘制拖动的形状变化
                Shape s=getShape(start.x,start.y,end.x,end.y);
                g2d.setPaint(colors.get(colors.size()-1));
                g2d.fill(s);
            }

            for(int i=0;i<shapes.size();i++){  //依次对形状填色
                Shape s=shapes.get(i);
                g2d.setPaint(colors.get(i));
                g2d.fill(s);
            }
        }

        private Color getColor(){  //获取颜色方法
            String color=button.getSelection().getActionCommand();  //读取当前颜色
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

        private Shape getShape(int x1,int y1,int x2,int y2){  //获取形状方法
            String shape=(String)combo.getSelectedItem();  //读取当前形状
            if(shape.equals("oval")){  //绘制椭圆
                return new Ellipse2D.Float(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
            }
            else if(shape.equals("rectangle")){  //绘制矩形
                return new Rectangle2D.Float(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
            }
            else if(shape.equals("triangle")){  //绘制等腰三角
                int x[]={x1,x2,(x1+x2)/2};
                int y[]={Math.max(y1,y2),Math.max(y1,y2),Math.min(y1,y2)};
                return new Polygon(x,y,3);
            }
            return null;
        }
    }

    public static void main(String[] args){  //主方法
        Graphic applet=new Graphic();
        JFrame frame=new JFrame("Graphic");
        frame.add(applet,BorderLayout.CENTER);
        frame.setSize(800,600);
        applet.init();
        applet.start();
        frame.setVisible(true);
    }
}