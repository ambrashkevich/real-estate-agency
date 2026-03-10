package org.example.agency.service;

import org.example.agency.model.Agent;
import org.example.agency.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;

    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    public Agent getAgentById(Long id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found with id: " + id));
    }

    public Agent createAgent(Agent agent) {
        if (agentRepository.findByEmail(agent.getEmail()).isPresent()) {
            throw new RuntimeException("Agent with email " + agent.getEmail() + " already exists");
        }
        if (agentRepository.findByLicenseNumber(agent.getLicenseNumber()).isPresent()) {
            throw new RuntimeException("Agent with license number " + agent.getLicenseNumber() + " already exists");
        }
        return agentRepository.save(agent);
    }

    public Agent updateAgent(Long id, Agent agentDetails) {
        Agent agent = getAgentById(id);

        agent.setFirstName(agentDetails.getFirstName());
        agent.setLastName(agentDetails.getLastName());
        agent.setPhone(agentDetails.getPhone());
        agent.setCommission(agentDetails.getCommission());

        return agentRepository.save(agent);
    }

    public void deleteAgent(Long id) {
        Agent agent = getAgentById(id);
        agentRepository.delete(agent);
    }
}
