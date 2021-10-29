<template>
	<v-row v-if="databaseType">
		<v-col cols="12" class="d-flex flex-row justify-space-between">
			<h2>{{ $t('systemsettings.database.title') }}</h2>
			<v-spacer></v-spacer>
			<v-btn fab icon @click="initDatabaseSection()">
				<v-icon>mdi-refresh</v-icon>
			</v-btn>
		</v-col>
        <v-col v-if="databaseLoading">
            <v-skeleton-loader type="list-item-three-line">
            </v-skeleton-loader>
        </v-col>
		<v-col cols="12" v-else>
			<v-row v-if="schemaVersion">
				<v-col cols="8">
					<p>{{ $t('systemsettings.database.schemaVersion') }}</p>
				</v-col>
				<v-col cols="4">
					<p>{{ schemaVersion }}</p>
				</v-col>

            </v-row>
            <v-row v-if="databaseSizeLoading">
                <v-col>
                    <v-skeleton-loader type="list-item-three-line">
                    </v-skeleton-loader>
                </v-col>
            </v-row>
            <v-row v-else>
				<v-col cols="8">
					<p>{{ $t('systemsettings.database.size') }}</p>
				</v-col>
				<v-col cols="4">
					<p>{{ databaseInfo.databaseSize }}</p>
				</v-col>

				<v-col cols="8">
					<p>{{ $t('systemsettings.database.file') }}</p>
				</v-col>
				<v-col cols="4">
					<p>{{ databaseInfo.filedataCount }}</p>
				</v-col>

				<v-col cols="8">
					<p>{{ $t('systemsettings.database.total') }}</p>
				</v-col>
				<v-col cols="4">
					<p>{{ databaseInfo.filedataSize }}</p>
				</v-col>

				<v-col cols="8">
					<p>{{ $t('systemsettings.database.histroy') }}</p>
				</v-col>
				<v-col cols="4">
					<p>{{ databaseInfo.historyCount }}</p>
				</v-col>

				<v-col cols="8">
					<p>{{ $t('systemsettings.database.event') }}</p>
				</v-col>
				<v-col cols="4">
					<p>{{ databaseInfo.eventCount }}</p>
				</v-col>

				<v-col cols="12">
					<p>{{ $t('systemsettings.database.top') }}</p>
				</v-col>
				<v-col
					cols="12"
					v-for="point in databaseInfo.topPoints"
					v-bind:key="point.pointId"
				>
					<p>
						{{ point.pointName }}<br />
						(Point ID: {{ point.pointId }} - count: {{ point.count }})
					</p>
				</v-col>
			</v-row>
		</v-col>
        
	</v-row>
</template>
<script>
export default {
    data() {
        return {
            databaseLoading: false,
            databaseSizeLoading: true,
        }
    },

    computed: {
        databaseType() {
			return this.$store.state.systemSettings.databaseType;
		},
		databaseInfo() {
			return this.$store.state.systemSettings.databaseInfo;
		},
        schemaVersion() {
			return this.$store.state.systemSettings.schemaVersion;
		},
    },

    mounted() {
        this.initDatabaseSection();
    },

    methods: {
        async initDatabaseSection() {
            this.databaseLoading = true;
            this.databaseSizeLoading = true;
            await this.$store.dispatch('getDatabaseType');
            await this.$store.dispatch('getSchemaVersion');
            this.databaseLoading = false;
            await this.$store.dispatch('getDatabaseSize');
            this.databaseSizeLoading = false;
		},
        saveDatabase(databaseType) {
			this.$store.dispatch('saveDatabaseType', databaseType).then((resp) => {
				if (resp) {
					this.generateNotification(
						'success',
						i18n.t('systemsettings.notification.save.database'),
					);
				} else {
					this.generateNotification('danger', i18n.t('systemsettings.notification.fail'));
				}
			});
		},

    }
};
</script>
<style></style>
