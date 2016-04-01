package de.trispeedys.enrollment.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.enrollment.dto.SimpleEventDTO;
import de.trispeedys.resourceplanning.entity.SimpleEvent;
import de.trispeedys.resourceplanning.repository.SimpleEventRepository;

@WebService
@SOAPBinding(style = Style.RPC)
public class EnrollmentService
{
    public SimpleEventDTO[] queryEnrollableEvents()
    {
        List<SimpleEvent> allEvents = RepositoryProvider.getRepository(SimpleEventRepository.class).findAll();
        List<SimpleEventDTO> dtos = new ArrayList<SimpleEventDTO>();
        SimpleEventDTO dto = null;
        for (SimpleEvent event : allEvents)
        {
            dto = new SimpleEventDTO();
            dto.setDescription(event.getDescription());
            dtos.add(dto);
        }
        return dtos.toArray(new SimpleEventDTO[dtos.size()]);
    }
}