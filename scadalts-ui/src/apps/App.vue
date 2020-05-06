<template>
    <div id="app">
        <input type="checkbox" id="check-sidebar">
        <label for="check-sidebar">
            <i class="fas fa-bars" id="btn-sidebar"></i>
            <i class="fas fa-times" id="cancel-sidebar"></i>
        </label>
        <div class="sidebar">
            <logo>Scada-LTS</logo>

            <a href="#"><span>Dashbord</span></a>
            <a href="#"><span>Watch lists</span></a>
            <a href="#"><span>Graphical Views</span></a>
            <a href="#"><span>Data Sources</span></a>
            <a href="#"><span>Services</span></a> <!-- sms etc..-->
            <a class="active" href="#/alarms"><span>Alarms</span></a>
            <a href="#"><span>Events</span></a>
            <a href="#"><span>Contact</span></a>
            <a href="#"><span>About</span></a>
            <a href="#"><span>Admin</span></a>
        </div>
        <div class="view">
            <router-view></router-view>
        </div>
    </div>

</template>

<script>

    export default {
        name: 'app',
        components: {
        }
    }
</script>

<style lang="scss">
   /* @import '../../node_modules/ag-grid-community/dist/styles/ag-grid.css';
    @import '../../node_modules/ag-grid-community/dist/styles/ag-theme-balham.css';*/

   :root {
       font-size: 16px;
       font-family: OpenSans-Regular;
       --text-primary: #b6b6b6;
       --text-secondary: #ececec;
       --bg-primary: #23232e;
       --bg-secondary: #141418;
       /* sidebar */
       --bg-logo: #1b1b1b;
       --bg-select-action: #1a75f6;
       --color-link: white;
       --color-logo: white;
       --letter-spacing: 1px;
   }
   * {
       margin: 0;
       padding: 0;
       text-decoration: none;
   }
   .sidebar{
       margin-top: -20px;
       position: fixed;
       left: -240px;
       width: 240px;
       height: 100%;
       background: var(--bg-secondary);
       transition: all .5s ease;
   }
   .sidebar logo {
       display: block;
       color: var(--color-logo);
       font-size: 1.4rem;
       text-align: center;
       background: var(--bg-logo);
       user-select: none;
       line-height: 3rem;
   }
   .sidebar a {
       display: block;
       color: var(--color-link);
       height: 2.4rem;
       line-height: 2.4rem;
       padding-left: 1.6rem;
       width: 90%;
   }
   .sidebar a:hover, a.active {
       background: var(--bg-select-action);
   }
   .sidebar a i {
       font-size: 1.2rem;
       margin-right: 1rem;
   }
   .sidebar a span{
       letter-spacing: var(--letter-spacing);
       text-transform: uppercase;
   }
   #check-sidebar {
       display: none;
   }

   label #btn-sidebar, label #cancel-sidebar {
       position: absolute;
       cursor: pointer;
       background: #262626;
       height: 45px;
       width: 45px;
       text-align: center;
       line-height: 45px;
       color: white;
       font-size: 29px;
       border-radius: 5px;
       margin: 15px 30px;
       border: 1px solid #262626;
       transition: all .5s ease;
   }
   label #cancel-sidebar {
       opacity:0;
       visibility: hidden;
   }
   #check-sidebar:checked ~ label #btn-sidebar {
       margin-top: -5px;
       margin-left: 245px;
       opacity: 0;
       visibility: hidden;
   }
   #check-sidebar:checked ~ label #cancel-sidebar {
       margin-left: 245px;
       opacity: 1;
       visibility: visible;
   }
   #check-sidebar:checked ~ .sidebar{
       left:0;
   }

   @media (max-width:860px) {
       .sidebar{
           height: auto;
           width: 55px;
           left: 0;
           margin: 100px 0;
       }
       .sidebar logo, #btn-sidebar, #cancel-sidebar {
           display: none;
       }
       span {
           position: absolute;
           margin-left: 23px;
           opacity: 0;
           visibility: hidden;
       }
       .sidebar a i {
           margin-left: -10px;
       }
       a:hover{
           width: 200px;
           background: inherit;
       }
       .sidebar a:hover span {
           opacity: 1;
           visibility: visible;
       }
       .sidebar a.active {
           background: var(--bg-select-action);
           width: 29px;
       }
   }

</style>
