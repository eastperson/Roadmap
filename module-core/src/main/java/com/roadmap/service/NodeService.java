package com.roadmap.service;

import com.roadmap.dto.roadmap.form.NodeAddForm;
import com.roadmap.dto.roadmap.form.NodeForm;
import com.roadmap.model.Node;
import com.roadmap.model.NodeType;
import com.roadmap.model.Roadmap;
import com.roadmap.model.Stage;
import com.roadmap.repository.NodeRepository;
import com.roadmap.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Transactional
@Service
@Log4j2
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;
    private final StageRepository stageRepository;
    private final ModelMapper modelMaper;


    public Long addNewNode(Stage stage, NodeAddForm nodeForm){
        nodeForm.setId(null);
        Node newNode = modelMaper.map(nodeForm,Node.class);
        newNode.setStage(stage);
        Node node = nodeRepository.save(newNode);
        return node.getId();
    }

    public Long addNewNode(Node parent, NodeAddForm nodeForm){
        log.info("------------------------add new node");
        log.info("------------------------parent : " + parent);
        log.info("------------------------nodeForm : " + nodeForm);
        nodeForm.setId(null);
        Node newNode = modelMaper.map(nodeForm,Node.class);
        log.info("new node : " + newNode);
        Node node = nodeRepository.save(newNode);
        log.info("node : " + node);
        newNode.setParent(parent);
        return node.getId();
    }

    public void removeNode(Node node) {

        if(node.getStage() != null){
            node.getStage().getNodeList().remove(node);
        }

        if(node.getParent() != null) {
            node.getParent().getChilds().remove(node);
        }

        nodeRepository.delete(node);
    }

    public Node modifyTypeNode(Node node,String type) {
        String nodeType = null;
        if(type.equalsIgnoreCase("text")) nodeType = NodeType.TEXT.toString();
        if(type.equalsIgnoreCase("post"))  nodeType = NodeType.POST.toString();
        if(type.equalsIgnoreCase("video"))  nodeType = NodeType.VIDEO.toString();
        node.setNodeType(nodeType);
        return nodeRepository.save(node);
    }
}
