set -o errexit
set -o nounset
set -o pipefail
set -o errtrace
backtrace() {
    local err="$?"
    set +x
    printf "%s\n\n" "*********************************************************************"
    printf "ERROR: %s:%i exited with code: %s\n\n" "${BASH_SOURCE[0]}" "${BASH_LINENO[0]}" "${err}"
    printf "%s\n\n" "${BASH_COMMAND:-}"
    for (( i = 0 ; i < ${#FUNCNAME[@]} - 1 ; i++ )) ; do
	printf "%-50s  %s:%i\n" "${FUNCNAME[$i+1]}()" "${BASH_SOURCE[$i+1]}" "${BASH_LINENO[$i]}"
    done
    exit "${err}"
}
trap 'backtrace' ERR
