import React from "react";
import Select, { buildMenuItems } from "./../../components/SelectDropdown";
const PreferredAlertChannelSelect: React.FC<{
  fieldName: string;
  label?: string;
  defaultValue?: string;
}> = ({ fieldName, label, defaultValue }) => {
  return (
    <Select
      defaultValue={defaultValue}
      fieldName={fieldName}
      label={label as string}
      menuItems={buildMenuItems(
        [
          { text: "Email", value: "Email" },
          { text: "Mobile", value: "Mobile", disabled: true },
        ],
        "Please choose preffered alert channel"
      )}
    />
  );
};
export default PreferredAlertChannelSelect;
