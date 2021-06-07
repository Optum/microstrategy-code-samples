package com.mstrweb.viewsql.tasks;

import com.microstrategy.utils.json.JSONArray;
import com.microstrategy.utils.json.JSONObject;
import com.microstrategy.web.beans.BeanFactory;
import com.microstrategy.web.beans.MarkupOutput;
import com.microstrategy.web.beans.RWBean;
import com.microstrategy.web.beans.RequestKeys;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.rw.RWDataSetDetails;
import com.microstrategy.web.objects.rw.RWDetailsFormatter;
import com.microstrategy.web.objects.rw.RWInstance;
import com.microstrategy.web.tasks.AbstractBaseTask;
import com.microstrategy.web.tasks.TaskException;
import com.microstrategy.web.tasks.TaskParameterMetadata;
import com.microstrategy.web.tasks.TaskRequestContext;
import java.util.List;

public class ViewSqlTask extends AbstractBaseTask {

  private static final String RESULT = "result";
  private TaskParameterMetadata beanState;

  public ViewSqlTask() {
    super("This task fetches the SQL for each dataset and returns it as a string.");
    super.addSessionStateParam(true, null);
    beanState = super.addParameterMetadata("beanState", "The bean state of the dossier.", true, null);
  }

  @Override
  public void processRequest(TaskRequestContext context, MarkupOutput markupOutput) throws TaskException {

    RequestKeys requestKeys = context.getRequestKeys();

    JSONObject response = new JSONObject();

    try {

      WebIServerSession webIServerSession =
        context.getWebIServerSession(AbstractBaseTask.PARAM_NAME_SESSION_STATE, null);

      RWBean rwBean = getRwBean();

      // Fetch the dossier
      rwBean.setSessionInfo(webIServerSession);
      rwBean.restoreState(beanState.getValue(requestKeys));
      // Fetch the document details via the formatter
      RWInstance rwInstance = rwBean.getRWInstance();
      RWDetailsFormatter rwDetailsFormatter = rwInstance.getDetailsFormatter();
      rwDetailsFormatter.setIncludeBasicDetailsForDataSets(true);
      rwDetailsFormatter.setIncludeDocumentDetails(true);
      rwDetailsFormatter.setIncludeSQLsforDataSets(true);

      List<RWDataSetDetails> listOfDataSets = rwInstance.getDocumentDetails().getDataSetDetails();

      JSONArray jsonArrayOfDataSets = new JSONArray();

      for (RWDataSetDetails rwDataSetDetails : listOfDataSets) {
        JSONObject dataSet = new JSONObject();
        dataSet.put("name", rwDataSetDetails.getName());
        dataSet.put("sql", rwDataSetDetails.getSQL());
        dataSet.put("totalRows", rwDataSetDetails.getTotalRows());
        jsonArrayOfDataSets.put(dataSet);
      }

      response.put(RESULT, jsonArrayOfDataSets);

      markupOutput.append(response);

    } catch (Exception e) {
      throw new TaskException("Unable to fetch SQL.");
    }

  }

  RWBean getRwBean() {
    return (RWBean) BeanFactory.getInstance().newBean("RWBean");
  }

}
