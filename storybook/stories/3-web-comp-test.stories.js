import { action } from '@storybook/addon-actions';
import "../my-web-components/webcomp.js";
// import "../my-web-components/app.js";

export default {
  title: 'Web Component Test',
  argTypes: {
    innerText: { control: 'text' },
  },
};

const Template = ({ onClick, innerText }) => {
  const el = document.createElement('component-x');
  el.p2 = innerText
  // el.style.height = "100px"
  el.addEventListener('click', onClick);
  return el;
};

export const myWebComp = Template.bind({});
myWebComp.args = {
  innerText: 'yellow',
  onClick: (e, f, g) => action('onClick')("xyz", "abc", e, f, g),
};
