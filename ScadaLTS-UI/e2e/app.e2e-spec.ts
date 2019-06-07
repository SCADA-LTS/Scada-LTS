import { ScadaLTSUIPage } from './app.po';

describe('scada-lts-ui App', function() {
  let page: ScadaLTSUIPage;

  beforeEach(() => {
    page = new ScadaLTSUIPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
