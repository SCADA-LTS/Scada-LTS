<template>
  <div class="container-fluid">
    <div class="row justify-content-center">
      <h1 class="col-xs-12">System Settings</h1>
    </div>
    <div v-if="systemInfoSettings" class="container-md">
      <h2>System Information</h2>
      <div class="col-md-6">
        <div class="col-xs-6">
          <p>Scada-LTS instance description</p>
        </div>
        <div class="col-xs-6">
          <input
            class="form-control"
            type="text"
            v-model="systemInfoSettings.instanceDescription"
          />
        </div>
      </div>
      <div class="col-md-6">
        <div class="col-xs-6">
          <p>System language</p>
        </div>
        <div class="col-xs-6">
          <select class="form-control" v-model="systemInfoSettings.language">
            <option value="de">Deutsch</option>
            <option value="en">English</option>
            <option value="ru">Руссиан</option>
            <option value="fi">Suomi</option>
            <option value="pl">Polski</option>
          </select>
        </div>
      </div>
    </div>
    <div v-if="systemEventTypes" class="col-md-6">
      <div class="row align-items-center">
        <h2 class="col-xs-11">System event alarm levels</h2>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="saveSystemEventAlarms()">
            <i class="glyphicon glyphicon-save"></i>
          </btn>
        </div>
      </div>
      <div>
        <div class="row">
          <div class="col-xs-6">
            <p>System startup</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="systemEventTypes[0].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="systemEventTypes[0].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="systemEventTypes[0].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="systemEventTypes[0].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="systemEventTypes[0].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>System shutdown</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="systemEventTypes[1].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="systemEventTypes[1].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="systemEventTypes[1].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="systemEventTypes[1].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="systemEventTypes[1].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Maximum alarm level changed</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="systemEventTypes[2].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="systemEventTypes[2].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="systemEventTypes[2].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="systemEventTypes[2].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="systemEventTypes[2].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>User login</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="systemEventTypes[3].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="systemEventTypes[3].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="systemEventTypes[3].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="systemEventTypes[3].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="systemEventTypes[3].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Compound detector failure</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="systemEventTypes[4].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="systemEventTypes[4].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="systemEventTypes[4].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="systemEventTypes[4].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="systemEventTypes[4].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Set point event handler failure</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="systemEventTypes[5].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="systemEventTypes[5].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="systemEventTypes[5].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="systemEventTypes[5].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="systemEventTypes[5].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Email send failure</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="systemEventTypes[6].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="systemEventTypes[6].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="systemEventTypes[6].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="systemEventTypes[6].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="systemEventTypes[6].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Point link failure</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="systemEventTypes[7].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="systemEventTypes[7].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="systemEventTypes[7].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="systemEventTypes[7].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="systemEventTypes[7].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Process event handler failure</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="systemEventTypes[8].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="systemEventTypes[8].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="systemEventTypes[8].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="systemEventTypes[8].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="systemEventTypes[8].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
      </div>
    </div>
    <div v-if="auditEventTypes" class="col-md-6">
      <div class="row align-items-center">
        <h2 class="col-xs-11">Audit event alarm levels</h2>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="saveAuditEventAlarms()">
            <i class="glyphicon glyphicon-save"></i>
          </btn>
        </div>
      </div>

      <div>
        <div class="row">
          <div class="col-xs-6">
            <p>Data source</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="auditEventTypes[0].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="auditEventTypes[0].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="auditEventTypes[0].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="auditEventTypes[0].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="auditEventTypes[0].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Data point</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="auditEventTypes[1].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="auditEventTypes[1].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="auditEventTypes[1].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="auditEventTypes[1].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="auditEventTypes[1].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Point event detector</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="auditEventTypes[2].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="auditEventTypes[2].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="auditEventTypes[2].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="auditEventTypes[2].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="auditEventTypes[2].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Compound event detector</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="auditEventTypes[3].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="auditEventTypes[3].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="auditEventTypes[3].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="auditEventTypes[3].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="auditEventTypes[3].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Scheduled event</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="auditEventTypes[4].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="auditEventTypes[4].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="auditEventTypes[4].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="auditEventTypes[4].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="auditEventTypes[4].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Event handler</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="auditEventTypes[5].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="auditEventTypes[5].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="auditEventTypes[5].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="auditEventTypes[5].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="auditEventTypes[5].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Point link</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="auditEventTypes[6].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="auditEventTypes[6].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="auditEventTypes[6].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="auditEventTypes[6].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="auditEventTypes[6].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Maintenance event</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="auditEventTypes[7].i2">
              <option v-bind:value="0">None</option>
              <option v-bind:value="1">Information</option>
              <option v-bind:value="2">Urgent</option>
              <option v-bind:value="3">Critical</option>
              <option v-bind:value="4">Life safety</option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="auditEventTypes[7].i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="auditEventTypes[7].i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="auditEventTypes[7].i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="auditEventTypes[7].i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
      </div>
    </div>
    <div v-if="httpSettings" class="col-md-6">
      <div class="row align-items-center">
        <h2 class="col-xs-11">Http Settings</h2>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="saveHttpSettings()">
            <i class="glyphicon glyphicon-save"></i>
          </btn>
        </div>
      </div>
      <div>
        <div class="row no-gutters">
          <div class="col-xs-12 no-gutters">
            <btn-group class="col-xs-12 no-gutters">
              <btn
                input-type="radio"
                :input-value="true"
                class="col-xs-6"
                v-model="httpSettings.useProxy"
                >Enable proxy</btn
              >
              <btn
                input-type="radio"
                :input-value="false"
                class="col-xs-6"
                v-model="httpSettings.useProxy"
                >Disable proxy</btn
              >
            </btn-group>
          </div>
        </div>
      </div>
      <div v-if="httpSettings.useProxy">
        <div class="row">
          <div class="col-xs-6">
            <p>Proxy host</p>
          </div>
          <div class="col-xs-6">
            <input
              class="form-control"
              type="text"
              v-model="httpSettings.host"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Proxy port</p>
          </div>
          <div class="col-xs-6">
            <input
              class="form-control"
              type="number"
              v-model="httpSettings.port"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Proxy username</p>
          </div>
          <div class="col-xs-6">
            <input
              class="form-control"
              type="text"
              v-model="httpSettings.username"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Proxy password</p>
          </div>
          <div class="col-xs-6">
            <input
              class="form-control"
              type="text"
              v-model="httpSettings.password"
            />
          </div>
        </div>
      </div>
    </div>
    <div v-if="emailSettings" class="col-md-6">
      <div class="row align-items-center">
        <h2 class="col-xs-11">Email Settings</h2>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="saveEmailSettings()">
            <i class="glyphicon glyphicon-save"></i>
          </btn>
        </div>
      </div>

      <div>
        <div class="row">
          <div class="col-xs-6">
            <p>SMTP host</p>
          </div>
          <div class="col-xs-6">
            <input
              class="form-control"
              type="text"
              v-model="emailSettings.host"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>SMTP port</p>
          </div>
          <div class="col-xs-6">
            <input
              class="form-control"
              type="number"
              v-model="emailSettings.port"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>From address</p>
          </div>
          <div class="col-xs-6">
            <input
              class="form-control"
              type="text"
              v-model="emailSettings.from"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>From name</p>
          </div>
          <div class="col-xs-6">
            <input
              class="form-control"
              type="text"
              v-model="emailSettings.name"
            />
          </div>
        </div>
        <div class="row no-gutters">
          <div class="col-xs-12">
            <btn-group class="col-xs-12">
              <btn
                input-type="radio"
                :input-value="true"
                v-model="emailSettings.auth"
                class="col-xs-6"
                >Use authorization</btn
              >
              <btn
                input-type="radio"
                :input-value="false"
                v-model="emailSettings.auth"
                class="col-xs-6"
                >Disable authorization</btn
              >
            </btn-group>
          </div>
        </div>
        <div v-if="emailSettings.auth">
          <div class="row">
            <div class="col-xs-6">
              <p>Username</p>
            </div>
            <div class="col-xs-6">
              <input
                class="form-control"
                type="text"
                v-model="emailSettings.username"
              />
            </div>
          </div>
          <div class="row">
            <div class="col-xs-6">
              <p>Password</p>
            </div>
            <div class="col-xs-6">
              <input
                class="form-control"
                type="text"
                v-model="emailSettings.password"
              />
            </div>
          </div>
        </div>
        <div class="row no-gutters">
          <div class="col-xs-12">
            <btn-group class="col-xs-12">
              <btn
                input-type="radio"
                :input-value="true"
                v-model="emailSettings.tls"
                class="col-xs-6"
                >Enable TLS</btn
              >
              <btn
                input-type="radio"
                :input-value="false"
                v-model="emailSettings.tls"
                class="col-xs-6"
                >Disable TLS</btn
              >
            </btn-group>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Content type</p>
          </div>
          <div class="col-xs-6">
            <select class="form-control" v-model="emailSettings.contentType">
              <option v-bind:value="0">HTML and text</option>
              <option v-bind:value="1">HTML only</option>
              <option v-bind:value="2">Text only</option>
            </select>
          </div>
        </div>
      </div>
    </div>
    <div v-if="miscSettings" class="col-md-6">
      <div class="row align-items-center">
        <h2 class="col-xs-11">Other Settings</h2>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="saveMiscSettings()">
            <i class="glyphicon glyphicon-save"></i>
          </btn>
        </div>
      </div>
      <div>
        <div class="row">
          <div class="col-xs-6">
            <p>UI performance</p>
          </div>
          <div class="col-xs-6">
            <select class="form-control" v-model="miscSettings.uiPerformance">
              <option v-bind:value="2000">High</option>
              <option v-bind:value="5000">Medium</option>
              <option v-bind:value="10000">Low</option>
            </select>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Purge events older than</p>
          </div>
          <div class="col-xs-3">
            <input
              class="form-control"
              type="number"
              v-model="miscSettings.eventPurgePeriods"
            />
          </div>
          <div class="col-xs-3">
            <select
              class="form-control"
              v-model="miscSettings.eventPurgePeriodType"
            >
              <option v-bind:value="4">day(s)</option>
              <option v-bind:value="5">week(s)</option>
              <option v-bind:value="6">month(s)</option>
              <option v-bind:value="7">year(s)</option>
            </select>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Purge report instances older than</p>
          </div>
          <div class="col-xs-3">
            <input
              class="form-control"
              type="number"
              v-model="miscSettings.reportPurgePeriods"
            />
          </div>
          <div class="col-xs-3">
            <select
              class="form-control"
              v-model="miscSettings.reportPurgePeriodType"
            >
              <option v-bind:value="4">day(s)</option>
              <option v-bind:value="5">week(s)</option>
              <option v-bind:value="6">month(s)</option>
              <option v-bind:value="7">year(s)</option>
            </select>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>Discard point values future dated more than</p>
          </div>
          <div class="col-xs-3">
            <input
              class="form-control"
              type="number"
              v-model="miscSettings.futureDateLimitPeriods"
            />
          </div>
          <div class="col-xs-3">
            <select
              class="form-control"
              v-model="miscSettings.futureDateLimitPeriodType"
            >
              <option v-bind:value="4">day(s)</option>
              <option v-bind:value="5">week(s)</option>
              <option v-bind:value="6">month(s)</option>
              <option v-bind:value="7">year(s)</option>
            </select>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import store from "../store";

export default {
  el: "#systemsettings",
  name: "systemsettings",
  mounted() {
    store.dispatch("getAllSettings");
  },
  methods: {
    saveEmailSettings() {
      this.$store.dispatch("saveEmailSettings").then((resp) => {
        if (resp) {
          this.generateNotification("success", "Email Settings saved!");
        } else {
          this.generateNotification("danger", "Something went wrong!");
        }
      });
    },
    saveHttpSettings() {
      this.$store.dispatch("saveHttpSettings").then((resp) => {
        if (resp) {
          this.generateNotification("success", "Http Settings saved!");
        } else {
          this.generateNotification("danger", "Something went wrong!");
        }
      });
    },
    saveMiscSettings() {
      this.$store.dispatch("saveMiscSettings").then((resp) => {
        if (resp) {
          this.generateNotification("success", "Other Settings saved!");
        } else {
          this.generateNotification("danger", "Something went wrong!");
        }
      });
    },
    saveSystemEventAlarms() {
      this.$store.dispatch("saveSystemEventTypes").then((resp) => {
        if (resp) {
          this.generateNotification(
            "success",
            "System Event Types Settings saved!"
          );
        } else {
          this.generateNotification("danger", "Something went wrong!");
        }
      });
    },
    saveAuditEventAlarms() {
      this.$store.dispatch("saveAuditEventTypes").then((resp) => {
        if (resp) {
          this.generateNotification(
            "success",
            "Audit Event Types Settings saved!"
          );
        } else {
          this.generateNotification("danger", "Something went wrong!");
        }
      });
    },
    generateNotification(type, content) {
      this.$notify({
        placement: "bottom-right",
        type,
        content,
      });
    },
  },
  computed: {
    databaseType() {
      return this.$store.state.systemSettings.databaseType;
    },
    systemInfoSettings() {
      return this.$store.state.systemSettings.systemInfoSettings;
    },
    emailSettings() {
      return this.$store.state.systemSettings.emailSettings;
    },
    httpSettings() {
      return this.$store.state.systemSettings.httpSettings;
    },
    miscSettings() {
      return this.$store.state.systemSettings.miscSettings;
    },
    auditEventTypes() {
      return this.$store.state.systemSettings.auditEventTypes;
    },
    systemEventTypes() {
      return this.$store.state.systemSettings.systemEventTypes;
    },
  },
};
</script>
<style lang="sass" scoped>
</style>