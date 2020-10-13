import { action } from '@storybook/addon-actions';

export default {
  title: 'Div',
  argTypes: {
    borderColor: { control: 'text' },
  },
};

const Template = ({ onClick, borderColor }) => {
  const el = document.createElement('div');
  el.style.border = "3px solid " + borderColor
  el.style.height = "100px"
  el.addEventListener('click', onClick);
  return el;
};

export const DivWithBorders = Template.bind({});
DivWithBorders.args = {
  borderColor: 'yellow',
  onClick: (e, f, g) => action('onClick')("xyz", "abc", e, f, g),
};
