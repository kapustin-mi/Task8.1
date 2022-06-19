package cs.vsu.ru.kapustin;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiMain extends JFrame {
    private com.mxgraph.view.mxGraph mxGraph;
    private Object parent;
    private List<mxCell> vertexes = new ArrayList<>();
    private List<mxCell> edges = new ArrayList<>();
    private JTextArea textAreaForInputPath;
    private JButton addTownButton;
    private JButton addPathButton;
    private JLabel outputInformationLabel;
    private JLabel outputWayLabel;
    private JButton executeButton;
    private JTextArea textAreaForInputData;

    public GuiMain() {
        this.setTitle("Task 8");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 600);

        mxGraph = new mxGraph();
        parent = mxGraph.getDefaultParent();
        Box panel = new Box(BoxLayout.Y_AXIS);
        mxGraphComponent graphComponent = new mxGraphComponent(mxGraph);

        initButtons();
        initLabels();
        initAreas();

        Box addTownBox = new Box(BoxLayout.X_AXIS);
        addTownBox.add(addTownButton);

        Box addPathBox = new Box(BoxLayout.X_AXIS);
        addPathBox.add(textAreaForInputPath);
        addPathBox.add(addPathButton);

        Box findAlmostShortestWayBox = new Box(BoxLayout.X_AXIS);
        findAlmostShortestWayBox.add(textAreaForInputData);
        findAlmostShortestWayBox.add(executeButton);

        Box outputInformationBox = new Box(BoxLayout.X_AXIS);
        outputInformationBox.add(outputInformationLabel);
        outputInformationBox.add(outputWayLabel);

        panel.add(graphComponent);
        panel.add(addTownBox);
        panel.add(addPathBox);
        panel.add(findAlmostShortestWayBox);
        panel.add(outputInformationBox);
        this.getContentPane().add(panel);
    }

    private void initAreas() {
        textAreaForInputData = new JTextArea();
        textAreaForInputData.setMaximumSize(new Dimension(400, 100));

        textAreaForInputPath = new JTextArea();
        textAreaForInputPath.setMaximumSize(new Dimension(400, 100));
    }

    private void initLabels() {
        outputInformationLabel = new JLabel("Almost the shortest way: ");
        outputWayLabel = new JLabel();
        outputWayLabel.setVisible(false);
    }

    private void initButtons() {
        addTownButton = new JButton("Add town");
        addTownButton.setMaximumSize(new Dimension(100, 100));
        addTownButton.addActionListener(e -> {
            mxGraph.getModel().beginUpdate();
            vertexes.add((mxCell) mxGraph.insertVertex(parent, null, vertexes.size(), 0, 10, 30, 30));
            mxGraph.getModel().endUpdate();
        });

        addPathButton = new JButton("Add path");
        addPathButton.setMaximumSize(new Dimension(100, 100));
        addPathButton.addActionListener(e -> {
            if (!textAreaForInputPath.getText().equals("")) {
                String[] path = textAreaForInputPath.getText().split(" ");

                Integer startIndex = Integer.parseInt(path[0]);
                Integer endIndex = Integer.parseInt(path[1]);
                Integer distance = Integer.parseInt(path[2]);

                if (!startIndex.equals(endIndex)) {
                    mxCell start = vertexes.get(startIndex);
                    mxCell end = vertexes.get(endIndex);
                    mxGraph.getModel().beginUpdate();

                    String edgeId = start.getValue() + " " + end.getValue();
                    for (mxCell edge : edges) {
                        String source = edge.getSource().getValue().toString();
                        String target = edge.getTarget().getValue().toString();

                        if ((source.equals(path[2])) && (target.equals(path[1]))) {
                            break;
                        }
                    }
                    edges.add((mxCell) mxGraph.insertEdge(parent, edgeId, distance, start, end));
                    mxGraph.getModel().endUpdate();
                }
            }
        });

        executeButton = new JButton("Execute");
        executeButton.setMaximumSize(new Dimension(100, 100));
        executeButton.addActionListener(e -> {
            if (!textAreaForInputData.getText().equals("")) {
                Graph graph = new Graph();

                for (mxCell edge : edges) {
                    String start = edge.getSource().getValue().toString();
                    String end = edge.getTarget().getValue().toString();
                    Integer distance = Integer.parseInt(edge.getValue().toString());
                    graph.addWay(start, end, distance);
                }

                String[] path = textAreaForInputData.getText().split(" ");
                String paths = graph.findAlmostShortestWay(path[0], path[1]);
                outputWayLabel.setText(paths);
                outputWayLabel.setVisible(true);
                paintPaths(paths);
            }
        });
    }

    private void paintPaths(String paths) {
        String[] ways = paths.split(", ");
        mxGraph.getModel().beginUpdate();

        for (String way : ways) {
            String[] dataOfWay = way.split(":");
            String[] points = dataOfWay[0].split("-");

            for (mxCell vertex : vertexes) {
                for (String point : points) {
                    if (vertex.getValue().toString().equals(point)) {
                        mxGraph.setCellStyle("ROUNDED;fillColor=green", new Object[]{vertex});
                    }
                }
            }
        }

        mxGraph.getModel().endUpdate();
    }
}
