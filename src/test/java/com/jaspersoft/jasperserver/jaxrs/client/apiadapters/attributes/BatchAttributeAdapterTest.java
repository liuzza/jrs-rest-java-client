package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.attributes;

import com.jaspersoft.jasperserver.dto.authority.ClientUserAttribute;
import com.jaspersoft.jasperserver.dto.authority.hypermedia.HypermediaAttribute;
import com.jaspersoft.jasperserver.dto.authority.hypermedia.HypermediaAttributesListWrapper;
import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestBuilder;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.RestClientConfiguration;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.MimeType;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.handling.DefaultErrorHandler;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest.buildRequest;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.internal.WhiteboxImpl.getInternalState;
import static org.powermock.reflect.internal.WhiteboxImpl.setInternalState;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNotSame;
import static org.testng.AssertJUnit.assertSame;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Unit tests for {@link com.jaspersoft.jasperserver.jaxrs.client.apiadapters.attributes.BatchAttributeAdapter}
 */

@PrepareForTest({BatchAttributeAdapter.class, JerseyRequest.class})
public class BatchAttributeAdapterTest extends PowerMockTestCase {

    private SessionStorage sessionStorageMock;
    private JerseyRequest<HypermediaAttributesListWrapper> jerseyRequestMock;
    private OperationResult<HypermediaAttributesListWrapper> operationResultMock;
    private RequestBuilder<HypermediaAttributesListWrapper> builderMock;

    @BeforeMethod
    public void before() {
        sessionStorageMock = mock(SessionStorage.class);
        jerseyRequestMock = mock(JerseyRequest.class);
        operationResultMock = mock(OperationResult.class);
        builderMock = mock(RequestBuilder.class);
    }

    @Test
    public void should_return_proper_operation_result() {

        // Given
        RestClientConfiguration configurationMock = mock(RestClientConfiguration.class);
        HypermediaAttributesListWrapper attributes = new HypermediaAttributesListWrapper();
        attributes.setProfileAttributes(asList(
                new HypermediaAttribute(new ClientUserAttribute().setName("max_threads").setValue("512")),
                new HypermediaAttribute(new ClientUserAttribute().setName("admin_cell_phone").setValue("03"))));

        mockStatic(JerseyRequest.class);
        when(buildRequest(
                eq(sessionStorageMock),
                eq(HypermediaAttributesListWrapper.class),
                eq(new String[]{"/", "attributes"}),
                any(DefaultErrorHandler.class))).thenReturn(jerseyRequestMock);
        doReturn(configurationMock).when(sessionStorageMock).getConfiguration();
        doReturn(MimeType.JSON).when(configurationMock).getContentMimeType();
        doReturn(jerseyRequestMock).when(jerseyRequestMock).setContentType(anyString());
        when(jerseyRequestMock.put(attributes)).thenReturn(operationResultMock);

        // When
        BatchAttributeAdapter adapter = new BatchAttributeAdapter("/", sessionStorageMock, attributes.getProfileAttributes().get(0).getName(), attributes.getProfileAttributes().get(1).getName());
        OperationResult<HypermediaAttributesListWrapper> retrieved = adapter.createOrUpdate(attributes);

        // Then
        assertNotNull(retrieved);
        assertSame(retrieved, operationResultMock);
        verifyStatic(times(1));
        buildRequest(
                eq(sessionStorageMock),
                eq(HypermediaAttributesListWrapper.class),
                eq(new String[]{"/" , "attributes"}),
                any(DefaultErrorHandler.class));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void should_throw_an_exception_when_query_params_were_not_set() {
        // Given
        HypermediaAttributesListWrapper attributes = new HypermediaAttributesListWrapper();
        attributes.setProfileAttributes(asList(
                new HypermediaAttribute(new ClientUserAttribute().setName("max_threads").setValue("512")),
                new HypermediaAttribute(new ClientUserAttribute().setName("admin_cell_phone").setValue("03"))));
        // When
        BatchAttributeAdapter adapter = new BatchAttributeAdapter("/", sessionStorageMock);
        adapter.createOrUpdate(attributes);
        // Then
        // should be thrown an exception
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void should_throw_an_exception_when_query_param_is_empty() {
        // Given
        HypermediaAttributesListWrapper attributes = new HypermediaAttributesListWrapper();
        attributes.setProfileAttributes(asList(
                new HypermediaAttribute(new ClientUserAttribute().setName("max_threads").setValue("512")),
                new HypermediaAttribute(new ClientUserAttribute().setName("admin_cell_phone").setValue("03"))));
        // When
        BatchAttributeAdapter adapter = new BatchAttributeAdapter("/", sessionStorageMock, new LinkedList<String>(asList("")));
        adapter.createOrUpdate(attributes);
        // Then
        // should be thrown an exception
    }

    @Test
    public void should_discard_extra_parameters() {

        // Given
        RestClientConfiguration configurationMock = mock(RestClientConfiguration.class);
        HypermediaAttributesListWrapper attributes = new HypermediaAttributesListWrapper();
        attributes.setProfileAttributes(asList(
                new HypermediaAttribute(new ClientUserAttribute().setName("max_threads").setValue("512")),
                new HypermediaAttribute(new ClientUserAttribute().setName("admin_cell_phone").setValue("03"))));

        HypermediaAttributesListWrapper newServerAttributes = new HypermediaAttributesListWrapper(attributes);
        newServerAttributes.getProfileAttributes().add((HypermediaAttribute) new HypermediaAttribute().setName("extra_attr_1").setValue("some_value_1"));
        newServerAttributes.getProfileAttributes().add((HypermediaAttribute) new HypermediaAttribute().setName("extra_attr_2").setValue("some_value_2"));
        newServerAttributes.getProfileAttributes().add((HypermediaAttribute) new HypermediaAttribute().setName("extra_attr_3").setValue("some_value_3"));


        mockStatic(JerseyRequest.class);
        when(buildRequest(
                eq(sessionStorageMock),
                eq(HypermediaAttributesListWrapper.class),
                eq(new String[]{"/", "attributes"}),
                any(DefaultErrorHandler.class))).thenReturn(jerseyRequestMock);
        doReturn(configurationMock).when(sessionStorageMock).getConfiguration();
        doReturn(MimeType.JSON).when(configurationMock).getContentMimeType();
        doReturn(jerseyRequestMock).when(jerseyRequestMock).setContentType(anyString());
        when(jerseyRequestMock.put(attributes)).thenReturn(operationResultMock);

        // When
        BatchAttributeAdapter adapter = new BatchAttributeAdapter("/", sessionStorageMock, attributes.getProfileAttributes().get(0).getName(), attributes.getProfileAttributes().get(1).getName());
        OperationResult<HypermediaAttributesListWrapper> retrieved = adapter.createOrUpdate(newServerAttributes);

        // Then
        assertNotNull(retrieved);
        assertSame(retrieved, operationResultMock);
        verifyStatic(times(1));
        buildRequest(
                eq(sessionStorageMock),
                eq(HypermediaAttributesListWrapper.class),
                eq(new String[]{"/" , "attributes"}),
                any(DefaultErrorHandler.class));
    }

    @Test
    public void should_delete_server_attributes() {

        // Given
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap<String, String>();
        map.add("key", "value");

        mockStatic(JerseyRequest.class);
        when(buildRequest(
                eq(sessionStorageMock),
                eq(HypermediaAttributesListWrapper.class),
                eq(new String[]{"/", "attributes"}),
                any(DefaultErrorHandler.class))).thenReturn(jerseyRequestMock);

        when(jerseyRequestMock.addParams(map)).thenReturn(builderMock);
        when(builderMock.delete()).thenReturn(operationResultMock);

        // When
        BatchAttributeAdapter adapter = new BatchAttributeAdapter("/", sessionStorageMock);
        setInternalState(adapter, "params", map);
        OperationResult<HypermediaAttributesListWrapper> retrieved = adapter.delete();

        // Then
        assertNotNull(retrieved);
        assertSame(retrieved, operationResultMock);
        verifyStatic(times(1));
        buildRequest(
                eq(sessionStorageMock),
                eq(HypermediaAttributesListWrapper.class),
                eq(new String[]{"/", "attributes"}),
                any(DefaultErrorHandler.class));
        verify(jerseyRequestMock, times(1)).addParams(map);
        verify(builderMock, times(1)).delete();
        verifyNoMoreInteractions(jerseyRequestMock, builderMock);
    }

    @Test
    public void should_retrieve_server_attributes() {

        // Given
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap<String, String>();
        map.add("key", "value");

        mockStatic(JerseyRequest.class);
        when(buildRequest(
                eq(sessionStorageMock),
                eq(HypermediaAttributesListWrapper.class),
                eq(new String[]{"/", "attributes"}),
                any(DefaultErrorHandler.class))).thenReturn(jerseyRequestMock);

        when(jerseyRequestMock.addParams(map)).thenReturn(builderMock);
        when(builderMock.get()).thenReturn(operationResultMock);

        // When
        BatchAttributeAdapter adapter = new BatchAttributeAdapter("/", sessionStorageMock);
        setInternalState(adapter, "params", map);
        OperationResult<HypermediaAttributesListWrapper> retrieved = adapter.get();

        // Then
        assertNotNull(retrieved);
        assertSame(retrieved, operationResultMock);
        verifyStatic(times(1));
        buildRequest(
                eq(sessionStorageMock),
                eq(HypermediaAttributesListWrapper.class),
                eq(new String[]{"/", "attributes"}),
                any(DefaultErrorHandler.class));
        verify(jerseyRequestMock, times(1)).addParams(map);
        verify(builderMock, times(1)).get();
        verifyNoMoreInteractions(jerseyRequestMock, builderMock);
    }


    @Test
    public void should_retrieve_server_attributes_with_permissions() {

        // Given
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap<String, String>();
        map.add("key", "value");

        mockStatic(JerseyRequest.class);
        when(buildRequest(
                eq(sessionStorageMock),
                eq(HypermediaAttributesListWrapper.class),
                eq(new String[]{"/", "attributes"}),
                any(DefaultErrorHandler.class))).thenReturn(jerseyRequestMock);
        RestClientConfiguration configurationMock = mock(RestClientConfiguration.class);
        when(sessionStorageMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getAcceptMimeType()).thenReturn(MimeType.JSON);
        when(jerseyRequestMock.addParam(anyString(), anyString())).thenReturn(jerseyRequestMock);
        when(jerseyRequestMock.addParams(map)).thenReturn(builderMock);
        when(builderMock.get()).thenReturn(operationResultMock);

        // When
        BatchAttributeAdapter adapter = new BatchAttributeAdapter("/", sessionStorageMock);
        setInternalState(adapter, "params", map);
        OperationResult<HypermediaAttributesListWrapper> retrieved = adapter.setIncludePermissions(true).get();

        // Then
        assertNotNull(retrieved);
        assertSame(retrieved, operationResultMock);
        verifyStatic(times(1));
        buildRequest(
                eq(sessionStorageMock),
                eq(HypermediaAttributesListWrapper.class),
                eq(new String[]{"/", "attributes"}),
                any(DefaultErrorHandler.class));
        verify(jerseyRequestMock, times(1)).addParams(map);
        verify(builderMock, times(1)).get();
        verify(jerseyRequestMock, times(1)).setAccept("application/hal+json");
        verify(jerseyRequestMock, times(1)).addParam("_embedded", "permission");
        verifyNoMoreInteractions(jerseyRequestMock, builderMock);
    }

    @Test
    public void should_set_params_as_vararg() {
        BatchAttributeAdapter adapter = new BatchAttributeAdapter("/", sessionStorageMock, "x", "y", "z");
        MultivaluedMap<String, String> params = getInternalState(adapter, "params");
        List<String> list = params.get("name");
        assertSame(list.size(), 3);
        assertTrue(list.contains("y") && list.contains("z"));
    }

    @Test
    public void should_set_params_as_collection() {
        BatchAttributeAdapter adapter = new BatchAttributeAdapter("/", sessionStorageMock, asList("x", "y", "z"));
        MultivaluedMap<String, String> params = getInternalState(adapter, "params");
        List<String> list = params.get("name");
        assertSame(list.size(), 3);
        assertTrue(list.contains("y") && list.contains("z"));
    }

    @Test
    public void should_retrieve_server_attributes_asynchronously() throws Exception {

        // Given
        BatchAttributeAdapter adapter = spy(new BatchAttributeAdapter("/", sessionStorageMock));
        doReturn(jerseyRequestMock).when(adapter, "buildRequest");
        doReturn(operationResultMock).when(jerseyRequestMock).get();

        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();

        final Callback<OperationResult<HypermediaAttributesListWrapper>, Void> callback = spy(new Callback<OperationResult<HypermediaAttributesListWrapper>, Void>() {
            @Override
            public Void execute(OperationResult<HypermediaAttributesListWrapper> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        doReturn(null).when(callback).execute(operationResultMock);

        // When
        RequestExecution retrieved = adapter.asyncGet(callback);

        synchronized (callback) {
            callback.wait(1000);
        }

        // Then
        verify(jerseyRequestMock).get();
        verify(callback).execute(operationResultMock);
        Assert.assertNotNull(retrieved);
        Assert.assertNotSame(currentThreadId, newThreadId.get());
    }

    @Test
    public void should_delete_atributes_asynchronously() throws Exception {

        // Given
        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();
        BatchAttributeAdapter adapterSpy = spy(new BatchAttributeAdapter("my/cool/uri", sessionStorageMock));

        final Callback<OperationResult<HypermediaAttributesListWrapper>, Void> callback = spy(new Callback<OperationResult<HypermediaAttributesListWrapper>, Void>() {
            @Override
            public Void execute(OperationResult<HypermediaAttributesListWrapper> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        doReturn(jerseyRequestMock).when(adapterSpy, "buildRequest");
        doReturn(operationResultMock).when(jerseyRequestMock).delete();
        doReturn(null).when(callback).execute(operationResultMock);

        // When
        RequestExecution retrieved = adapterSpy.asyncDelete(callback);

        // Wait
        synchronized (callback) {
            callback.wait(1000);
        }

        // Then
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
        verify(callback, times(1)).execute(operationResultMock);
        verify(jerseyRequestMock, times(1)).delete();
    }

    @Test
    public void should_createOrUpdate_atributes_asynchronously() throws Exception {

        // Given
        HypermediaAttributesListWrapper listWrapperMock = mock(HypermediaAttributesListWrapper.class);
        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();
        BatchAttributeAdapter adapterSpy = spy(new BatchAttributeAdapter("my/cool/uri", sessionStorageMock));

        final Callback<OperationResult<HypermediaAttributesListWrapper>, Void> callback = spy(new Callback<OperationResult<HypermediaAttributesListWrapper>, Void>() {
            @Override
            public Void execute(OperationResult<HypermediaAttributesListWrapper> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        doReturn(jerseyRequestMock).when(adapterSpy, "buildRequest");
        doReturn(jerseyRequestMock).when(adapterSpy, "buildRequest");
        doReturn(operationResultMock).when(jerseyRequestMock).put(listWrapperMock);
        doReturn(null).when(callback).execute(operationResultMock);

        // When
        RequestExecution retrieved = adapterSpy.asyncCreateOrUpdate(listWrapperMock, callback);

        // Wait
        synchronized (callback) {
            callback.wait(1000);
        }

        // Then
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
        verify(callback, times(1)).execute(operationResultMock);
        verify(jerseyRequestMock, times(1)).put(listWrapperMock);
    }

    @AfterMethod
    public void after() {
        sessionStorageMock = null;
        jerseyRequestMock = null;
        operationResultMock = null;
        builderMock = null;
    }
}