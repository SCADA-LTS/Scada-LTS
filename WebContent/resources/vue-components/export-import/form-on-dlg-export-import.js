Vue.use(VueForm);

Vue.component('button-counter', {
    data: function () {
        return {
            count: 0
        }
    },
    template: '<button v-on:click="count++">You clicked me {{ count }} times.</button>'
})


Vue.component('simple-form', {
    data: {
        formstate: {},
        model: {
            name: '',
            email: ''
        }
    },
    methods: {
        onSubmit: function () {
            console.log(this.formstate.$valid);
        }
    },
    template: `<vue-form :state="formstate" @submit.prevent="onSubmit">
           <div v-if="formstate.$submittedState && formstate.$submittedState.$invalid" class="error-list">
                <field-messages tag="ul" :state="formstate.$submittedState" auto-label name="name">
                <li slot="required">
            <label>Name is a required field</label>
        </li>
      </field-messages>
      <field-messages tag="ul" :state="formstate.$submittedState" auto-label name="email">
        <li slot="required">
          <label>Email is a required field</label>
        </li>
        <li slot="email">
          <label>Email is invalid</label>
        </li>
      </field-messages>
    </div>

    <validate auto-label class="form-block">
      <label>Name *</label>
      <input v-model="model.name" name="name" required />
    </validate>

    <validate auto-label class="form-block">
      <label>Email *</label>
      <input type="email" v-model="model.email" name="email" required />
    </validate>

    <button type="submit">Submit</button>

  </vue-form>

  <pre>{{formstate}}</pre>
    `

});