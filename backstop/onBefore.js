module.exports = async (page, scenario, vp) => {
  await require('./loadCookies')(page, scenario);
  await require('./scadaLogin')(page, scenario, '__username__', '__password__');
};
