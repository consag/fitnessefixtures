
package com.informatica.wsh;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.informatica.wsh package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetAllRepositories_QNAME = new QName("http://www.informatica.com/wsh", "GetAllRepositories");
    private final static QName _GetAllTaskInstances_QNAME = new QName("http://www.informatica.com/wsh", "GetAllTaskInstances");
    private final static QName _Logout_QNAME = new QName("http://www.informatica.com/wsh", "Logout");
    private final static QName _GetAllDIServers_QNAME = new QName("http://www.informatica.com/wsh", "GetAllDIServers");
    private final static QName _GetAllFolders_QNAME = new QName("http://www.informatica.com/wsh", "GetAllFolders");
    private final static QName _LoginReturn_QNAME = new QName("http://www.informatica.com/wsh", "LoginReturn");
    private final static QName _GetAllDIServersReturn_QNAME = new QName("http://www.informatica.com/wsh", "GetAllDIServersReturn");
    private final static QName _WSHFaultDetails_QNAME = new QName("http://www.informatica.com/wsh", "WSHFaultDetails");
    private final static QName _Login_QNAME = new QName("http://www.informatica.com/wsh", "Login");
    private final static QName _GetAllFoldersReturn_QNAME = new QName("http://www.informatica.com/wsh", "GetAllFoldersReturn");
    private final static QName _GetAllRepositoriesReturn_QNAME = new QName("http://www.informatica.com/wsh", "GetAllRepositoriesReturn");
    private final static QName _GetAllTaskInstancesReturn_QNAME = new QName("http://www.informatica.com/wsh", "GetAllTaskInstancesReturn");
    private final static QName _LogoutReturn_QNAME = new QName("http://www.informatica.com/wsh", "LogoutReturn");
    private final static QName _Context_QNAME = new QName("http://www.informatica.com/wsh", "Context");
    private final static QName _GetAllWorkflows_QNAME = new QName("http://www.informatica.com/wsh", "GetAllWorkflows");
    private final static QName _GetAllWorkflowsReturn_QNAME = new QName("http://www.informatica.com/wsh", "GetAllWorkflowsReturn");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.informatica.wsh
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FaultDetails }
     * 
     */
    public FaultDetails createFaultDetails() {
        return new FaultDetails();
    }

    /**
     * Create an instance of {@link LoginRequest }
     * 
     */
    public LoginRequest createLoginRequest() {
        return new LoginRequest();
    }

    /**
     * Create an instance of {@link FolderInfoArray }
     * 
     */
    public FolderInfoArray createFolderInfoArray() {
        return new FolderInfoArray();
    }

    /**
     * Create an instance of {@link DIServerInfoArray }
     * 
     */
    public DIServerInfoArray createDIServerInfoArray() {
        return new DIServerInfoArray();
    }

    /**
     * Create an instance of {@link VoidRequest }
     * 
     */
    public VoidRequest createVoidRequest() {
        return new VoidRequest();
    }

    /**
     * Create an instance of {@link GetAllTaskInstancesRequest }
     * 
     */
    public GetAllTaskInstancesRequest createGetAllTaskInstancesRequest() {
        return new GetAllTaskInstancesRequest();
    }

    /**
     * Create an instance of {@link WorkflowInfoArray }
     * 
     */
    public WorkflowInfoArray createWorkflowInfoArray() {
        return new WorkflowInfoArray();
    }

    /**
     * Create an instance of {@link SessionHeader }
     * 
     */
    public SessionHeader createSessionHeader() {
        return new SessionHeader();
    }

    /**
     * Create an instance of {@link FolderInfo }
     * 
     */
    public FolderInfo createFolderInfo() {
        return new FolderInfo();
    }

    /**
     * Create an instance of {@link TaskInstanceInfoArray }
     * 
     */
    public TaskInstanceInfoArray createTaskInstanceInfoArray() {
        return new TaskInstanceInfoArray();
    }

    /**
     * Create an instance of {@link VoidResponse }
     * 
     */
    public VoidResponse createVoidResponse() {
        return new VoidResponse();
    }

    /**
     * Create an instance of {@link RepositoryInfoArray }
     * 
     */
    public RepositoryInfoArray createRepositoryInfoArray() {
        return new RepositoryInfoArray();
    }

    /**
     * Create an instance of {@link TaskInstanceInfo }
     * 
     */
    public TaskInstanceInfo createTaskInstanceInfo() {
        return new TaskInstanceInfo();
    }

    /**
     * Create an instance of {@link WorkflowInfo }
     * 
     */
    public WorkflowInfo createWorkflowInfo() {
        return new WorkflowInfo();
    }

    /**
     * Create an instance of {@link DIServerInfo }
     * 
     */
    public DIServerInfo createDIServerInfo() {
        return new DIServerInfo();
    }

    /**
     * Create an instance of {@link RepositoryInfo }
     * 
     */
    public RepositoryInfo createRepositoryInfo() {
        return new RepositoryInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VoidRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "GetAllRepositories")
    public JAXBElement<VoidRequest> createGetAllRepositories(VoidRequest value) {
        return new JAXBElement<VoidRequest>(_GetAllRepositories_QNAME, VoidRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllTaskInstancesRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "GetAllTaskInstances")
    public JAXBElement<GetAllTaskInstancesRequest> createGetAllTaskInstances(GetAllTaskInstancesRequest value) {
        return new JAXBElement<GetAllTaskInstancesRequest>(_GetAllTaskInstances_QNAME, GetAllTaskInstancesRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VoidRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "Logout")
    public JAXBElement<VoidRequest> createLogout(VoidRequest value) {
        return new JAXBElement<VoidRequest>(_Logout_QNAME, VoidRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VoidRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "GetAllDIServers")
    public JAXBElement<VoidRequest> createGetAllDIServers(VoidRequest value) {
        return new JAXBElement<VoidRequest>(_GetAllDIServers_QNAME, VoidRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VoidRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "GetAllFolders")
    public JAXBElement<VoidRequest> createGetAllFolders(VoidRequest value) {
        return new JAXBElement<VoidRequest>(_GetAllFolders_QNAME, VoidRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "LoginReturn")
    public JAXBElement<String> createLoginReturn(String value) {
        return new JAXBElement<String>(_LoginReturn_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DIServerInfoArray }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "GetAllDIServersReturn")
    public JAXBElement<DIServerInfoArray> createGetAllDIServersReturn(DIServerInfoArray value) {
        return new JAXBElement<DIServerInfoArray>(_GetAllDIServersReturn_QNAME, DIServerInfoArray.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FaultDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "WSHFaultDetails")
    public JAXBElement<FaultDetails> createWSHFaultDetails(FaultDetails value) {
        return new JAXBElement<FaultDetails>(_WSHFaultDetails_QNAME, FaultDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LoginRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "Login")
    public JAXBElement<LoginRequest> createLogin(LoginRequest value) {
        return new JAXBElement<LoginRequest>(_Login_QNAME, LoginRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FolderInfoArray }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "GetAllFoldersReturn")
    public JAXBElement<FolderInfoArray> createGetAllFoldersReturn(FolderInfoArray value) {
        return new JAXBElement<FolderInfoArray>(_GetAllFoldersReturn_QNAME, FolderInfoArray.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RepositoryInfoArray }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "GetAllRepositoriesReturn")
    public JAXBElement<RepositoryInfoArray> createGetAllRepositoriesReturn(RepositoryInfoArray value) {
        return new JAXBElement<RepositoryInfoArray>(_GetAllRepositoriesReturn_QNAME, RepositoryInfoArray.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TaskInstanceInfoArray }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "GetAllTaskInstancesReturn")
    public JAXBElement<TaskInstanceInfoArray> createGetAllTaskInstancesReturn(TaskInstanceInfoArray value) {
        return new JAXBElement<TaskInstanceInfoArray>(_GetAllTaskInstancesReturn_QNAME, TaskInstanceInfoArray.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VoidResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "LogoutReturn")
    public JAXBElement<VoidResponse> createLogoutReturn(VoidResponse value) {
        return new JAXBElement<VoidResponse>(_LogoutReturn_QNAME, VoidResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SessionHeader }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "Context")
    public JAXBElement<SessionHeader> createContext(SessionHeader value) {
        return new JAXBElement<SessionHeader>(_Context_QNAME, SessionHeader.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FolderInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "GetAllWorkflows")
    public JAXBElement<FolderInfo> createGetAllWorkflows(FolderInfo value) {
        return new JAXBElement<FolderInfo>(_GetAllWorkflows_QNAME, FolderInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WorkflowInfoArray }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.informatica.com/wsh", name = "GetAllWorkflowsReturn")
    public JAXBElement<WorkflowInfoArray> createGetAllWorkflowsReturn(WorkflowInfoArray value) {
        return new JAXBElement<WorkflowInfoArray>(_GetAllWorkflowsReturn_QNAME, WorkflowInfoArray.class, null, value);
    }

}
