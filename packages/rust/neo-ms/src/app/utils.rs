#[macro_export]
macro_rules! str {
    ($str:expr) => {
        format!("\"{}\"", $str.to_string())
    };
}

/// Correlator is a struct that contains the string value of the correlator
pub struct Correlator;

impl Correlator {
    pub const SYSTEM: &'static str = "\"system\"";
}
