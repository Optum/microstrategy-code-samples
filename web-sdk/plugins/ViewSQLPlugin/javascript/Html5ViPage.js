window.mstrWebUrl = [location.protocol, '//', location.host, location.pathname].join('');
window.taskUrl = mstrWebUrl.replace("mstrWeb", mstrConfig.taskURL);

if (mstrmojo.vi.controllers.DocumentController) {
  // Ignore unused, referenced in MojoMenubarFileModel.xml
  mstrmojo.vi.controllers.DocumentController.prototype.viewSql = function viewSql() {
    loadViewSqlModal();
  };
}

window.renderViewSqlWindow = function (data) {

  const viewSqlModalBody = document.getElementById("viewSqlModalBody");

  if (data.result) {

    const dataSets = data.result;

    for (let i = 0; i < dataSets.length; i++) {
      const dataSetEntry = dataSets[i];
      const dataSetHtml = '<label for="dataset_' + i + '" class="view-sql-label">' + dataSetEntry.name + ': ' + dataSetEntry.totalRows + ' rows</label><br>' +
        '<textarea id="dataset_' + i + '" name=dataset_"' + i + '" class="view-sql-textarea">' + dataSetEntry.sql + '</textarea><br>';
      $(viewSqlModalBody).append(dataSetHtml);
    }

  }

  document.getElementById("viewSqlModal").style.display = "block";

};

window.closeWaitBox = function () {
  const waitBox = document.getElementById(mstrmojo.all.waitBox.id);
  waitBox.firstChild.style.display = "none";
};

window.setBackgroundIsUsable = function (isUsable) {
  const rootView = document.getElementById(mstrmojo.all.rootView.id);

  if (isUsable) {
    rootView.style.opacity = "1";
    rootView.style.pointerEvents = "";
  } else {
    rootView.style.opacity = "0.5";
    rootView.style.pointerEvents = "none";
  }

};

window.loadSqlDataSuccess = function (data) {

  const modal = document.createElement('div');
  modal.id = "modal_container";

  closeWaitBox();
  setBackgroundIsUsable(false);

  // Load the modal
  $(modal).load("../plugins/ViewSQLPlugin/html/viewSqlModal.html", function () {
    document.body.appendChild(modal);
    renderViewSqlWindow(data);
  });

};

window.loadSqlDataFailure = function () {
  closeWaitBox();
  mstrmojo.error();
};

window.loadViewSqlModal = function () {

  // Open wait box
  const waitBox = document.getElementById(mstrmojo.all.waitBox.id);
  waitBox.firstChild.style.display = "";

  const callbacks = {
    success: loadSqlDataSuccess,
    failure: loadSqlDataFailure
  };

  const params = {
    taskId: "viewSqlTask",
    sessionState: mstrApp.sessionState,
    beanState: mstrApp.docModelData.bs
  };

  mstrmojo.xhr.request("POST", taskUrl, callbacks, params);

};

window.closeViewSqlModal = function () {
  const viewSqlModalContainer = document.getElementById("modal_container");
  if (viewSqlModalContainer) {
    document.body.removeChild(viewSqlModalContainer);
    setBackgroundIsUsable(true);
  }
};
