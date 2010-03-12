

<div id="wrapper">
    <div id="header">
        <div id="mambo">
            <img alt="Logo" src="<%=request.getContextPath()%>/images/registry.png" />
        </div>
    </div>
</div>




<%
    boolean isLoggedIn = (request.getSession().getAttribute("login") != null && request.getSession().getAttribute("login").equals("true") && request.getSession().getAttribute("username") != null);
%>


<table width="100%" class="menubar" cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td class="menubackgr">&nbsp;</td>
    <td class="menubackgr">

        <div id="myMenuID"></div>

        <!--* view status-->
        <!--* insert metadata & files-->
        <!--* obtain metadata & files-->
        <!--* search repository-->
        <!--* log in-->
        <!--# reload repository-->
        <!--# log out-->
        <script language="JavaScript" type="text/javascript">
		var myMenu =
		[			
			[null,'New',null,null,'New',
				['<img src="<%=request.getContextPath()%>/includes/js/ThemeOffice/content.png" />','Add New repository','<%=request.getContextPath()%>/form/addNewRepository.jsp',null,'Add New repository'],
			],	
			_cmSplit,
			[null,'Search',null,null,'Search',
				['<img src="<%=request.getContextPath()%>/includes/js/ThemeOffice/query.png" />','Search registry','<%=request.getContextPath()%>/search/',null,'Search registry'],
			],		
<%			
if (isLoggedIn) {
%>			_cmSplit,			
			[null,'Insert-Modify',null,null,'Insert-Modify',
				['<img src="<%=request.getContextPath()%>/includes/js/ThemeOffice/content.png" />','Insert-Modify Metadata','<%=request.getContextPath()%>/admin/updateMetadata.jsp',null,'Insert-Modify Metadata'],
			],			
			_cmSplit,
			[null,'Management',null,null,'Management',
				['<img src="<%=request.getContextPath()%>/includes/js/ThemeOffice/config.png" />','Configuration Wizard','<%=request.getContextPath()%>/init/index.jsp',null,''],
				['<img src="<%=request.getContextPath()%>/includes/js/ThemeOffice/config.png" />','Change Configuration','<%=request.getContextPath()%>/admin/changeConfiguration.jsp',null,''],
				['<img src="<%=request.getContextPath()%>/includes/js/ThemeOffice/config.png" />','Manage harvester Configuration','<%=request.getContextPath()%>/registryMgmt/index.jsp',null,''],
				['<img src="<%=request.getContextPath()%>/includes/js/ThemeOffice/config.png" />','Add New Harvester','<%=request.getContextPath()%>/registryMgmt/registerNewHarvester.jsp',null,''],
			],
			_cmSplit,
			[null,'Status',null,null,'Status',
				['<img src="<%=request.getContextPath()%>/includes/js/ThemeOffice/checkin.png" />','Test Status','<%=request.getContextPath()%>/status/',null,''],
				['<img src="<%=request.getContextPath()%>/includes/js/ThemeOffice/config.png" />','Reload repository','<%=request.getContextPath()%>/admin/reload.jsp',null,''],
             	['<img src="<%=request.getContextPath()%>/includes/js/ThemeOffice/index.png" />','Recreate Index','<%=request.getContextPath()%>/admin/index-lucene.jsp',null,'Recreate Index'],
             	['<img src="<%=request.getContextPath()%>/includes/js/ThemeOffice/index.png" />','Optimize Index','<%=request.getContextPath()%>/admin/optimize-lucene.jsp',null,'Optimize Index'],
         ],
			_cmSplit,
<%
		}
%>
		];
		cmDraw ('myMenuID', myMenu, 'hbr', cmThemeOffice, 'ThemeOffice');
		</script>

    </td>
    <td class="menubackgr" align="right">
    </td>
    <td class="menubackgr" align="right">
<%
        if (!isLoggedIn) {
%>
            <a href="<%=request.getContextPath()%>/login/login.jsp" style="color: #333333; font-weight: bold; padding-right:5px;">LOG IN</a>
<%
        } else {
%>
            <a href="<%=request.getContextPath()%>/login/logout.jsp" style="color: #333333; font-weight: bold; padding-right:5px;">LOG OUT</a>
<%
        }
%>
    </td>
    </tr>
</table>






