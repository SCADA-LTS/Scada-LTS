<template>

    <v-list nav dense>
        <v-list-item>
            <v-list-item-avatar>
                <v-img src=""></v-img>
            </v-list-item-avatar>
        </v-list-item>
        <v-list-item link href="watch_list.shtm">
            <v-list-item-icon>
                <v-icon>mdi-desktop-mac</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Watch List
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="views.shtm">
            <v-list-item-icon>
                <v-icon>mdi-application</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Graphic View
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="synopticPanel">
            <v-list-item-icon>
                <v-icon>mdi-apps-box</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Synoptic Panel
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="events.shtm">
            <v-list-item-icon>
                <v-icon>mdi-flag-variant</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Alarms
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="reports.shtm">
            <v-list-item-icon>
                <v-icon>mdi-book</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Reports
            </v-list-item-title>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item link href="event_handlers.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-settings</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Event Handlers
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="data_sources.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-database</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Data Sources
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="scheduled_events.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-clock-outline</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Scheduled Events
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="compound_events.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-bell-ring</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Compound Event Detectors
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="point_links.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-link-variant</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Point Links
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="scripting.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-script</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Scripting
            </v-list-item-title>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item link href="users.shtm">
            <v-list-item-icon>
                <v-icon>mdi-account</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Users
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="usersProfiles.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-account-card-details</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                User profiles
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="pointHierarchySLTS" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-folder-multiple</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Point Hierarchy
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="mailing_lists.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-email</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Mailing List
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="publishers.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-remote</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Publishers
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="maintenance_events.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-hammer</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Maintenance Events
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="system_settings.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-wrench</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                System settings
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="emport.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-swap-vertical-bold</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Import/Export
            </v-list-item-title>
        </v-list-item>
        <v-list-item link href="sql.shtm" v-if="isAdmin">
            <v-list-item-icon>
                <v-icon>mdi-script-text-outline</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                SQL
            </v-list-item-title>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item link href="logout.htm">
            <v-list-item-icon>
                <v-icon>mdi-location-exit</v-icon>
            </v-list-item-icon>
            <v-list-item-title>
                Logout
            </v-list-item-title>
        </v-list-item>
    </v-list>

</template>
<script>
    export default {
        computed: {
            isAdmin() {
                return this.$store.state.adminPrivilege
            }
        }
    }

</script>
<style>

</style>