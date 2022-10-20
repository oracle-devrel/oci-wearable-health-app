import * as React from "react";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select, { SelectChangeEvent } from "@mui/material/Select";
import { Theme, SxProps } from "@mui/material";

interface MenuItemConfig {
  value: string | number | null;
  text: string | number;
  disabled?: boolean;
}
export const buildMenuItems = (
  items: MenuItemConfig[],
  placeHolderOptionText?: string
) => {
  return (
    !!placeHolderOptionText
      ? [
          {
            text: placeHolderOptionText,
            value: placeHolderOptionText,
            disabled: true,
          },
          ...items,
        ]
      : items
  ).map((item, i) => {
    return (
      <MenuItem
        disabled={item?.disabled}
        key={`${i}-${item?.value}`}
        value={item.value as any}
      >
        {item.text}
      </MenuItem>
    );
  });
};

const SelectAutoWidth: React.FC<{
  fieldName: string;
  menuItems: React.ReactNode;
  id?: string;
  label: string;
  sx?: SxProps<Theme>;
  defaultValue?: string | number;
}> = ({ fieldName, menuItems, id, label, sx, defaultValue }) => {
  const [val, setVal] = React.useState(defaultValue ?? "");

  const handleChange = (event: SelectChangeEvent) => {
    setVal(event.target.value);
  };

  return (
    <div>
      <FormControl fullWidth={true} sx={sx ?? null}>
        {!!label && <InputLabel id={id ?? fieldName}>{label}</InputLabel>}
        <Select
          labelId={id ?? fieldName}
          id={id ?? fieldName}
          value={val as string}
          onChange={handleChange}
          fullWidth
          label={label}
          name={fieldName}
          defaultValue={defaultValue as string}
        >
          {menuItems}
        </Select>
      </FormControl>
    </div>
  );
};

export default SelectAutoWidth;
