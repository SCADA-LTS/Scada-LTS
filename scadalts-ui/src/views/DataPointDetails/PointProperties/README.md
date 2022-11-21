# Data Point Details module

Data Point Details Vue.js page is is the same as Point Details JSP page from classic User Interface but it use REST API to manage the datapoint data. Using that page user is able to modify the properties of specific Data Point and be up-to-date with latest changes of that object. The main purpose of that page is migration from DWR stack to Vue.js approach. Layout of that page is likely the same as it was on old UI but some small UX improvments has been done.

Separation from JSP file allow further developer to easly create small Vue.js components that can be implemented to extend the functionality of this page.

Point Properties is a additional component that allow Scada-LTS users to modify the DataPoint properties. It is prepared as a seperate component.

**This page do not provide the final functionality!**

Done by Radoslaw Jajko <rjajko@softq.pl>

## Works to be done in future:

- User access and previlages.
- Related graphical views.
- Configurable chart.
